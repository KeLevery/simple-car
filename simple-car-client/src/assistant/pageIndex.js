import router from '@/router'

let cachedIndex = null

function normalizeKeywords(keywords) {
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
export function getPageIndex() {
	if (cachedIndex) return cachedIndex

	const routes = (router && router.options && Array.isArray(router.options.routes)) ? router.options.routes : []

	cachedIndex = routes
		.filter(r => r && typeof r.path === 'string')
		.filter(r => r.path !== '/') // exclude login
		.map(r => {
			const meta = r.meta || {}
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
