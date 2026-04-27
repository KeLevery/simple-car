<template>
		<div class="community-container">
			<!-- Header -->
			<div class="comm-header">
				<div class="header-left">
					<div class="header-indicator"></div>
					<span class="header-label">COMMUNITY</span>
				</div>
				<div class="header-right">
					<van-icon name="search" size="18" class="search-icon" />
				</div>
			</div>
			<h1 class="page-title">车主社区</h1>

			<!-- 热门话题轮播 -->
			<div class="banner-section animate-enter stagger-1">
				<van-swipe class="my-swipe" :autoplay="3000" indicator-color="var(--accent)" :show-indicators="true">
					<van-swipe-item v-for="(item, index) in banners" :key="index">
						<div class="banner-card" :style="{ background: item.color }">
							<div class="banner-info">
								<div class="topic-tag"># {{ item.tag }}</div>
								<div class="banner-title">{{ item.title }}</div>
							</div>
							<van-icon :name="item.icon" class="banner-bg-icon" />
						</div>
					</van-swipe-item>
				</van-swipe>
			</div>

			<!-- 社区分类 -->
			<van-tabs v-model="activeTab" sticky offset-top="0" color="var(--accent)" line-width="20" background="transparent" class="comm-tabs">
				<van-tab title="精选">
					<div class="feed-list">
						<div v-for="(post, index) in feeds" :key="index" class="post-card">
							<div class="post-header" @click="openPostDetail(post)">
								<div class="avatar-wrapper">
									<van-image round width="38" height="38" :src="post.avatar" />
								</div>
								<div class="user-meta">
									<div class="nickname">{{ post.nickname }}</div>
									<div class="time">{{ post.time }}</div>
								</div>
								<div class="follow-chip" @click.stop>+ 关注</div>
							</div>
							<div class="post-content" @click="openPostDetail(post)">
								<div class="post-text">{{ post.content }}</div>
								<div class="post-images" v-if="post.images && post.images.length">
									<van-image v-for="(img, i) in post.images" :key="i" width="31%" height="100" radius="8" :src="img" fit="cover" />
								</div>
							</div>
							<div class="post-footer">
								<div class="action-item" @click.stop="sharePost(post)">
									<van-icon name="share-o" />
									<span>{{ post.share }}</span>
								</div>
								<div class="action-item" @click.stop="openPostDetail(post)">
									<van-icon name="comment-o" />
									<span>{{ post.comment }}</span>
								</div>
								<div class="action-item" :class="{ 'liked': post.isLiked }" @click.stop="toggleLike(post)">
									<van-icon :name="post.isLiked ? 'good-job' : 'good-job-o'" />
									<span>{{ post.like }}</span>
								</div>
							</div>
						</div>
					</div>
				</van-tab>
				<van-tab title="广场">
					<van-empty image="network" description="广场动态正在加载中..." />
				</van-tab>
				<van-tab title="车友圈">
					<van-empty image="search" description="快来加入你的城市车友会吧" />
				</van-tab>
			</van-tabs>

			<!-- 发布弹窗 -->
			<van-popup v-model="showPublish" position="bottom" round class="sci-popup">
				<div class="publish-popup">
					<div class="publish-title">发布动态</div>
					<van-field v-model="publishContent" rows="4" autosize type="textarea" placeholder="分享你的用车体验..." class="publish-textarea" />
					<div class="publish-btn-wrap" @click="publishPost">
						<van-button class="publish-submit" block :loading="publishing">发布</van-button>
					</div>
				</div>
			</van-popup>

			<!-- 发布按钮 -->
			<div class="fab-btn" @click="showPublish = true">
				<van-icon name="plus" />
			</div>

			<!-- 评论/详情弹窗 -->
			<van-popup v-model="showComments" position="bottom" round class="comment-popup" :style="{ maxHeight: '85%' }" safe-area-inset-bottom>
				<div class="comment-sheet">
					<div class="comment-sheet__header">
						<div class="comment-sheet__title">详情</div>
						<van-icon name="cross" class="comment-sheet__close" @click="showComments = false" />
					</div>
					<div class="comment-sheet__body">
						<!-- Post detail -->
						<div v-if="activePost" class="post-detail">
							<div class="post-detail__header">
								<van-image round width="34" height="34" :src="activePost.avatar" />
								<div class="post-detail__meta">
									<div class="post-detail__name">{{ activePost.nickname }}</div>
									<div class="post-detail__time">{{ activePost.time }}</div>
								</div>
							</div>
							<div class="post-detail__text">{{ activePost.content }}</div>
							<div class="post-detail__images" v-if="activePost.images && activePost.images.length">
								<van-image v-for="(img, i) in activePost.images" :key="i" width="31%" height="90" radius="8" :src="img" fit="cover" />
							</div>
							<div class="post-detail__divider"></div>
						</div>

						<!-- Comments -->
						<van-loading v-if="commentsLoading" size="24px" vertical>加载中...</van-loading>
						<div v-else>
							<div v-if="comments.length" class="comment-list">
								<div v-for="c in comments" :key="c.id" class="comment-item">
									<div class="comment-item__avatar">
										<van-image round width="32" height="32" :src="c.avatar || defaultAvatar" />
									</div>
									<div class="comment-item__content">
										<div class="comment-item__meta">
											<span class="comment-item__name">{{ c.nickname || ('用户' + c.userId) }}</span>
											<span class="comment-item__time">{{ formatCommentTime(c.createTime) }}</span>
										</div>
										<div class="comment-item__text">{{ c.content }}</div>
									</div>
								</div>
							</div>
							<van-empty v-else description="暂无评论，快来抢沙发" />
						</div>
					</div>
					<div class="comment-sheet__footer" @click.stop>
						<van-field v-model="commentDraft" placeholder="写下你的评论..." clearable>
							<template #button>
								<van-button size="small" type="primary" :loading="commentSubmitting" @click.stop="submitComment">发送</van-button>
							</template>
						</van-field>
					</div>
				</div>
			</van-popup>

			<Tabbar active="/community" />
		</div>
	</template>

	<script>
	import Tabbar from "@/components/Tabbar.vue"
	import { postList, toggleLike, createPost, commentList, createComment } from '@/api/community'

	export default {
		components: { Tabbar },
		data() {
			return {
				activeTab: 0,
				banners: [
					{ title: '极氪001续航大测评，邀你来挑战', tag: '续航挑战赛', color: 'linear-gradient(135deg, #eef2ff, #dbeafe)', icon: 'fire-o' },
					{ title: '春日出游计划：分享你的自驾路线', tag: '春日自驾', color: 'linear-gradient(135deg, #ecfdf5, #d1fae5)', icon: 'flower-o' },
					{ title: 'OTA升级体验报告：新功能好用吗？', tag: '系统升级', color: 'linear-gradient(135deg, #fff7ed, #ffedd5)', icon: 'upgrade' }
				],
				feeds: [],
				showPublish: false,
				publishContent: '',
				publishing: false,

				// comments
				showComments: false,
				activePost: null,
				comments: [],
				commentsLoading: false,
				commentDraft: '',
				commentSubmitting: false,
				defaultAvatar: 'https://img01.yzcdn.cn/vant/cat.jpeg'
			}
		},
		created() {
			this.fetchPosts();
		},
		methods: {
			async fetchPosts() {
				const res = await postList();
				if (res.code === 200) {
					this.feeds = (res.data || []).map(item => ({
						...item,
						images: item.images ? item.images.split(',') : [],
						avatar: item.avatar || 'https://img01.yzcdn.cn/vant/cat.jpeg',
						time: '刚刚',
						share: item.shareCount,
						comment: item.commentCount,
						like: item.likeCount
					}));
				}
			},
			async toggleLike(post) {
				const res = await toggleLike(post.id);
				if (res.code === 200) {
					post.isLiked = !post.isLiked;
					post.isLiked ? post.like++ : post.like--;
				}
			},
			async publishPost() {
				if (!this.publishContent.trim()) {
					this.$toast('请输入内容');
					return;
				}
				this.publishing = true;
				try {
					const res = await createPost({ content: this.publishContent });
					if (res.code === 200) {
						this.$toast.success('发布成功');
						this.showPublish = false;
						this.publishContent = '';
						this.fetchPosts();
					}
				} catch (error) {
					console.error(error);
				} finally {
					this.publishing = false;
				}
			},
			sharePost() {
				this.$toast('分享功能开发中');
			},
			formatCommentTime(t) {
				if (!t) return '';
				const s = String(t).replace('T', ' ');
				return s.length >= 16 ? s.slice(0, 16) : s;
			},
			openPostDetail(post) {
				this.openComments(post);
			},
			async openComments(post) {
				this.activePost = post;
				this.showComments = true;
				this.commentsLoading = true;
				this.comments = [];
				try {
					const res = await commentList(post.id);
					if (res.code === 200) {
						this.comments = res.data || [];
					}
				} finally {
					this.commentsLoading = false;
				}
			},
			async submitComment() {
				if (!this.activePost) return;
				const content = (this.commentDraft || '').trim();
				if (!content) {
					this.$toast('请输入评论内容');
					return;
				}
				this.commentSubmitting = true;
				try {
					const res = await createComment(this.activePost.id, { content });
					if (res.code === 200) {
						this.commentDraft = '';
						this.$toast.success('评论成功');
						await this.openComments(this.activePost);
						if (typeof this.activePost.comment === 'number') {
							this.activePost.comment++;
						}
					}
				} finally {
					this.commentSubmitting = false;
				}
			}
		}
	}
	</script>

	<style lang="scss" scoped>
	.community-container {
		min-height: 100vh;
		background: var(--bg-page);
		padding-bottom: 80px;
		position: relative;
	}

	/* ===== Header ===== */
	.comm-header {
		position: relative;
		z-index: 5;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 16px 20px 0;
		padding-top: calc(env(safe-area-inset-top, 20px) + 12px);

		.header-left {
			display: flex;
			align-items: center;
			gap: 8px;
		}

		.header-indicator {
			width: 4px;
			height: 4px;
			border-radius: 50%;
			background: var(--accent);
		}

		.header-label {
			font-size: 10px;
			letter-spacing: 3px;
			color: var(--text-tertiary);
			font-weight: 500;
		}

		.header-right {
			padding: 6px;
			.search-icon {
				color: var(--text-secondary);
			}
		}
	}

	.page-title {
		position: relative;
		z-index: 5;
		font-size: 28px;
		font-weight: 600;
		letter-spacing: 1px;
		color: var(--text-primary);
		margin: 16px 20px 0;
	}

	/* ===== Tabs ===== */
	.comm-tabs {
		position: relative;
		z-index: 5;
	}

	::v-deep .van-tabs__nav {
		background-color: transparent !important;
	}

	::v-deep .van-tab {
		color: var(--text-secondary);
		font-weight: 400;
		&.van-tab--active {
			color: var(--text-primary);
			font-weight: 600;
		}
	}

	::v-deep .van-tabs__line {
		background: var(--accent) !important;
		height: 3px !important;
		border-radius: 3px;
	}

	/* ===== Banner ===== */
	.banner-section {
		position: relative;
		z-index: 5;
		padding: 20px 20px 10px;

		.my-swipe {
			border-radius: 16px;
			overflow: hidden;
		}

		.banner-card {
			height: 120px;
			padding: 20px;
			position: relative;

			.topic-tag {
				font-size: 11px;
				background: rgba(43, 108, 176, 0.08);
				border: 1px solid rgba(43, 108, 176, 0.15);
				display: inline-block;
				padding: 2px 10px;
				border-radius: 4px;
				margin-bottom: 10px;
				color: var(--accent);
				font-weight: 500;
			}

			.banner-title {
				font-size: 17px;
				font-weight: 600;
				max-width: 80%;
				line-height: 1.4;
				color: #1e293b;
			}

			.banner-bg-icon {
				position: absolute;
				right: 15px;
				bottom: 10px;
				font-size: 60px;
				color: var(--accent);
				opacity: 0.12;
			}
		}
	}

	/* ===== Feed List ===== */
	.feed-list {
		padding: 10px 20px;

		.post-card {
			background: var(--panel-bg);
			backdrop-filter: blur(var(--panel-blur));
			-webkit-backdrop-filter: blur(var(--panel-blur));
			border: var(--panel-border);
			border-radius: 16px;
			padding: 18px;
			margin-bottom: 14px;
			box-shadow: var(--card-shadow);
			transition: transform 0.2s;

			.post-header {
				display: flex;
				align-items: center;
				margin-bottom: 14px;

				.avatar-wrapper {
					padding: 1px;
					border: 1px solid #e2e8f0;
					border-radius: 50%;
					display: flex;
					align-items: center;
					justify-content: center;
				}

				.user-meta {
					flex: 1;
					margin-left: 12px;
					.nickname {
						font-size: 15px;
						font-weight: 600;
						color: var(--text-primary);
					}
					.time {
						font-size: 12px;
						color: var(--text-tertiary);
						margin-top: 2px;
					}
				}

				.follow-chip {
					font-size: 12px;
					color: var(--accent);
					border: 1px solid var(--accent);
					border-radius: 14px;
					padding: 4px 14px;
					font-weight: 500;
					cursor: pointer;
					transition: all 0.2s;

					&:active {
						background: rgba(43, 108, 176, 0.05);
						transform: scale(0.95);
					}
				}
			}

			.post-content {
				margin-bottom: 14px;
				.post-text {
					font-size: 15px;
					color: var(--text-secondary);
					line-height: 1.6;
					margin-bottom: 12px;
				}
				.post-images {
					display: flex;
					flex-wrap: wrap;
					gap: 8px;
				}
			}

			.post-footer {
				display: flex;
				justify-content: space-between;
				border-top: 1px solid var(--border);
				padding-top: 14px;

				.action-item {
					display: flex;
					align-items: center;
					color: var(--text-tertiary);
					font-size: 13px;
					gap: 6px;
					cursor: pointer;

					.van-icon {
						font-size: 18px;
					}

					&.liked {
						color: var(--accent);
					}
				}
			}
		}
	}

	/* ===== Publish Popup ===== */
	.sci-popup {
		background: #ffffff !important;
		border-top: 1px solid var(--border);
		padding: 24px !important;
	}

	.publish-popup {
		.publish-title {
			font-size: 18px;
			font-weight: 600;
			color: var(--text-primary);
			text-align: center;
			margin-bottom: 20px;
		}
		.publish-textarea {
			background: var(--bg-secondary) !important;
			border: 1px solid var(--border);
			border-radius: 12px;
			margin-bottom: 20px;
			padding: 12px;

			::v-deep .van-field__control {
				color: var(--text-primary);
				font-size: 15px;
				&::placeholder {
					color: var(--text-tertiary);
				}
			}
		}

		.publish-submit {
			background: linear-gradient(135deg, var(--accent), #1a365d) !important;
			border: none !important;
			color: #ffffff !important;
			font-weight: 600;
			height: 48px;
			font-size: 15px;
			border-radius: 12px;
		}
	}

	/* ===== FAB ===== */
	.fab-btn {
		position: fixed;
		right: 20px;
		bottom: 85px;
		width: 54px;
		height: 54px;
		background: linear-gradient(135deg, var(--accent), #1a365d);
		color: #ffffff;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24px;
		box-shadow: 0 4px 12px rgba(43, 108, 176, 0.3);
		z-index: 99;
		transition: all 0.2s;
		cursor: pointer;

		&:active {
			transform: scale(0.9);
			box-shadow: 0 2px 6px rgba(43, 108, 176, 0.2);
		}
	}

	/* Vant overrides */
	::v-deep .van-empty__description {
		color: var(--text-tertiary) !important;
	}

	::v-deep .van-swipe__indicator {
		width: 6px;
		height: 6px;
		border-radius: 50%;
		background: #cbd5e1;
		opacity: 1;

		&.van-swipe__indicator--active {
			background: var(--accent);
		}
	}

	/* ===== Comment/Detail Popup ===== */
	.comment-popup {
		z-index: 2000;
		background: #ffffff !important;
	}

	.comment-sheet {
		display: flex;
		flex-direction: column;
		height: 85vh;
	}

	.comment-sheet__header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 14px 16px;
		border-bottom: 1px solid var(--border);
		flex: 0 0 auto;
	}

	.comment-sheet__title {
		font-size: 16px;
		font-weight: 600;
		color: var(--text-primary);
	}

	.comment-sheet__close {
		font-size: 18px;
		color: var(--text-secondary);
		padding: 6px;
	}

	.comment-sheet__body {
		flex: 1 1 auto;
		overflow-y: auto;
		padding: 12px 16px;
	}

	.comment-sheet__footer {
		flex: 0 0 auto;
		padding: 10px 12px;
		border-top: 1px solid var(--border);
		background: #ffffff;
	}

	.post-detail {
		margin-bottom: 8px;
	}

	.post-detail__header {
		display: flex;
		align-items: center;
		gap: 10px;
		margin-bottom: 10px;
	}

	.post-detail__meta {
		display: flex;
		flex-direction: column;
		gap: 2px;
	}

	.post-detail__name {
		font-size: 14px;
		font-weight: 600;
		color: var(--text-primary);
	}

	.post-detail__time {
		font-size: 12px;
		color: var(--text-tertiary);
	}

	.post-detail__text {
		font-size: 15px;
		color: var(--text-secondary);
		line-height: 1.6;
		margin-bottom: 10px;
	}

	.post-detail__images {
		display: flex;
		flex-wrap: wrap;
		gap: 8px;
		margin-bottom: 10px;
	}

	.post-detail__divider {
		height: 1px;
		background: var(--border);
		margin: 8px 0 2px;
		opacity: 0.8;
	}

	.comment-list {
		display: flex;
		flex-direction: column;
		gap: 12px;
		padding-top: 6px;
	}

	.comment-item {
		display: flex;
		gap: 10px;
	}

	.comment-item__meta {
		display: flex;
		align-items: baseline;
		gap: 8px;
		margin-bottom: 4px;
	}

	.comment-item__name {
		font-size: 13px;
		font-weight: 600;
		color: var(--text-primary);
	}

	.comment-item__time {
		font-size: 11px;
		color: var(--text-tertiary);
	}

	.comment-item__text {
		font-size: 14px;
		color: var(--text-secondary);
		line-height: 1.5;
		word-break: break-word;
	}

	/* ===== Animations ===== */
	.animate-enter {
		opacity: 0;
		transform: translateY(10px);
		animation: enterUp 0.6s forwards ease-out;
	}

	.stagger-1 {
		animation-delay: 0.1s;
	}

	@keyframes enterUp {
		to {
			opacity: 1;
			transform: translateY(0);
		}
	}
	</style>
