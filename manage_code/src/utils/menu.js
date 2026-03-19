import toolUtil from '@/utils/toolUtil'
const menu = {
	list() {
		const raw = toolUtil.storageGet("menus")
		if (!raw) return null
		try {
			const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw
			if (!Array.isArray(parsed)) return null
			return parsed
				.filter(role => role && role.tableName != 'jiazhang' && role.roleName != '家长')
				.map(role => {
					const next = { ...role }
					const filterSections = (sections) => {
						if (!Array.isArray(sections)) return sections
						return sections
							.map(section => {
								if (!section || !Array.isArray(section.child)) return section
								const child = section.child.filter(item => item && item.tableName != 'jiazhang')
								return { ...section, child }
							})
							.filter(section => section && Array.isArray(section.child) ? section.child.length > 0 : true)
					}
					next.backMenu = filterSections(next.backMenu)
					next.frontMenu = filterSections(next.frontMenu)
					return next
				})
		} catch (e) {
			return null
		}
	}
}
export default menu;
