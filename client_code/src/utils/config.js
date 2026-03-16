const config = {
    get() {
        return {
            url : process.env.VUE_APP_BASE_API_URL + process.env.VUE_APP_BASE_API + '/',
            name: process.env.VUE_APP_BASE_API,
			menuList:[
			]
        }
    },
    getProjectName(){
        return {
            projectName: "家校合作平台"
        } 
    }
}
export default config
