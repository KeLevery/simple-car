import router from '@/router'

export interface PageIndexItem {
	path: string
	title: string
	keywords: string[]
	pinned: boolean
	pinnedOrder: number
}

// 路由 meta 的自定义字段（title/keywords/pinned/pinnedOrder），用宽松类型消费
interface LoosePageMeta {
	title?: unknown
	keywords?: unknown
	pinned?: unknown
	pinnedOrder?: unknown
	[key: string]: unknown
}

let cachedIndex: PageIndexItem[] | null = null

function normalizeKeywords(keywords: unknown): string[] {
	if (!Array.isArray(keywords)) return []
	return keywords
		.filter(Boolean)
		.map(k => String(k).trim())
		.filter(Boolean)
}

/**
 * Build a searchable page index from VueRouter routes.
 * Returns: [{ path, title, keywords }]
 */
export function getPageIndex(): PageIndexItem[] {
	if (cachedIndex) return cachedIndex

	const routes = (router && router.options && Array.isArray(router.options.routes)) ? router.options.routes : []

	cachedIndex = routes
		.filter(r => r && typeof r.path === 'string')
		.filter(r => r.path !== '/') // exclude login
		.map(r => {
			const meta = (r.meta || {}) as LoosePageMeta
			const title = meta.title || r.name || r.path
			return {
				path: r.path,
				title: String(title),
				keywords: normalizeKeywords(meta.keywords),
				pinned: Boolean(meta.pinned),
				pinnedOrder: typeof meta.pinnedOrder === 'number' ? meta.pinnedOrder : 0
			}
		})

	return cachedIndex
}
