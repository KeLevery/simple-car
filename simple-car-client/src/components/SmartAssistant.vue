<template>
	<div>
		<!-- Floating entry -->
		<div class="sa-fab" @click="open = true">
			<van-icon name="chat-o" size="22" />
		</div>

		<!-- Popup -->
		<van-popup v-model="open" position="bottom" round class="sa-popup" safe-area-inset-bottom>
			<div class="sa-header">
				<div class="sa-title">智能助手</div>
				<van-icon name="cross" class="sa-close" @click="open = false" />
			</div>

			<van-search
				v-model="query"
				placeholder="输入关键词，例如：救援、维保、订单、设置"
				show-action
				@search="onSearch"
				@cancel="onCancel"
				@input="onInput"
			/>

			<div class="sa-content">
				<!-- Recent / Recommended when empty query -->
				<div v-if="!normalizedQuery">
					<div v-if="recentPages.length" class="sa-section">
						<div class="sa-section-title">最近访问</div>
						<van-cell-group inset>
							<van-cell
								v-for="item in recentPages"
								:key="item.path"
								:title="item.title"
																is-link
								@click="goto(item.path)"
							/>
						</van-cell-group>
					</div>

					<div v-else class="sa-section">
						<div class="sa-section-title">推荐入口</div>
						<van-cell-group inset>
							<van-cell
								v-for="item in pinnedPages"
								:key="item.path"
								:title="item.title"
																is-link
								@click="goto(item.path)"
							/>
						</van-cell-group>
					</div>
				</div>

				<!-- Search results -->
				<van-cell-group v-else-if="results.length > 0" inset>
					<van-cell
						v-for="item in results"
						:key="item.path"
						:title="item.title"
						:label="item.reason"
												is-link
						@click="goto(item.path)"
					/>
				</van-cell-group>

				<van-empty v-else description="没有找到相关页面" />
			</div>
		</van-popup>
	</div>
</template>

<script>
import { getPageIndex } from '@/assistant/pageIndex'

const RECENT_KEY = 'sa_recent_pages_v1'

function normalizeQuery(q) {
	return String(q || '').trim().toLowerCase()
}

function includesText(hay, needle) {
	if (!hay || !needle) return false
	return String(hay).toLowerCase().includes(needle)
}

export default {
	name: 'SmartAssistant',
	data() {
		return {
			open: false,
			query: '',
			results: [],
			recentPages: [],
			pinnedPages: [],
			recentMax: 5
		}
	},
	computed: {
		normalizedQuery() {
			return normalizeQuery(this.query)
		}
	},
	watch: {
		open(val) {
			if (val) {
				this.refreshResults()
			}
		}
	},
	methods: {
		readRecentPaths() {
			try {
				const raw = window.localStorage.getItem(RECENT_KEY)
				const arr = JSON.parse(raw || '[]')
				if (!Array.isArray(arr)) return []
				return arr.filter(p => typeof p === 'string' && p)
			} catch (e) {
				return []
			}
		},
		writeRecentPaths(paths) {
			window.localStorage.setItem(RECENT_KEY, JSON.stringify(paths.slice(0, this.recentMax)))
		},
		pushRecentPath(path) {
			const paths = this.readRecentPaths().filter(p => p !== path)
			paths.unshift(path)
			this.writeRecentPaths(paths)

			// keep UI in sync if popup is open
			this.refreshRecentAndPinned(getPageIndex())
		},
		refreshRecentAndPinned(pages) {
			const byPath = new Map(pages.map(p => [p.path, p]))
			const recentPaths = this.readRecentPaths()

			this.recentPages = recentPaths
				.map(p => byPath.get(p))
				.filter(Boolean)
				.slice(0, this.recentMax)

			const recentSet = new Set(recentPaths)
			this.pinnedPages = pages
				.filter(p => p && p.pinned)
				.filter(p => !recentSet.has(p.path))
				.sort((a, b) => (a.pinnedOrder || 0) - (b.pinnedOrder || 0))
		},
		onCancel() {
			this.open = false
		},
		onSearch() {
			this.refreshResults()
		},
		onInput() {
			if (this.open) {
				this.refreshResults()
			}
		},
		refreshResults() {
			const q = normalizeQuery(this.query)
			const pages = getPageIndex()
			this.refreshRecentAndPinned(pages)

			if (!q) {
				this.results = []
				return
			}

			const scored = pages
				.map(p => {
					const title = p.title || ''
					const keywords = Array.isArray(p.keywords) ? p.keywords : []
					const path = p.path || ''

					let score = 0
					const reasons = []

					if (includesText(title, q)) {
						score += 10
						reasons.push('标题匹配')
					}

					const hitKeyword = keywords.find(k => includesText(k, q))
					if (hitKeyword) {
						score += 6
						reasons.push('关键词：' + hitKeyword)
					}

					if (includesText(path, q)) {
						score += 2
						reasons.push('路径匹配')
					}

					return { ...p, score, reason: reasons.join(' · ') }
				})
				.filter(p => p.score > 0)
				.sort((a, b) => b.score - a.score)
				.slice(0, 5)

			this.results = scored
		},
		goto(path) {
			this.open = false
			this.query = ''
			this.results = []
			this.pushRecentPath(path)
			this.$nextTick(() => {
				this.$router.push({ path })
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.sa-fab {
	position: fixed;
	right: 16px;
	bottom: 86px; /* leave space for Tabbar */
	width: 48px;
	height: 48px;
	border-radius: 50%;
	background: var(--accent);
	color: #fff;
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 6px 18px rgba(43, 108, 176, 0.35);
	z-index: 200;
}

.sa-popup {
	min-height: 55vh;
	max-height: 75vh;
	overflow: hidden;
}

.sa-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 16px 6px;
}

.sa-title {
	font-size: 16px;
	font-weight: 600;
	color: var(--text-primary);
}

.sa-close {
	font-size: 18px;
	color: var(--text-tertiary);
	padding: 6px;
}

.sa-content {
	padding: 10px 12px 18px;
	overflow: auto;
	max-height: calc(75vh - 120px);
}
</style>
