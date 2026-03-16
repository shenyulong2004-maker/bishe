	import {
		createRouter,
		createWebHashHistory
	} from 'vue-router'
	import news from '@/views/news/list'
	import jiazhang from '@/views/jiazhang/list'
	import xuesheng from '@/views/xuesheng/list'
	import examquestion from '@/views/exam/examquestion/list'
	import jiaoshi from '@/views/jiaoshi/list'
	import xueshengchengji from '@/views/xueshengchengji/list'
	import exampaper from '@/views/exampaper/list'
	import users from '@/views/users/list'
	import exampaperlist from '@/views/exam/exampaperlist/list'
	import examination from '@/views/exam/exampaperlist/examination'
	import examrecord from '@/views/exam/examrecord/list'
	import examfailrecord from '@/views/exam/examfailrecord/list'
	import xueshengRegister from '@/views/xuesheng/register'
	import xueshengCenter from '@/views/xuesheng/center'
	import jiazhangRegister from '@/views/jiazhang/register'
	import jiazhangCenter from '@/views/jiazhang/center'
	import jiaoshiRegister from '@/views/jiaoshi/register'
	import jiaoshiCenter from '@/views/jiaoshi/center'

export const routes = [{
		path: '/login',
		name: 'login',
		component: () => import('../views/login.vue')
},{
		path: '/',
		name: 'index',
		component: () => import('../views/index'),
		children: [{
			path: '/',
			name: '首页',
			component: () => import('../views/HomeView.vue'),
			meta: {
				affix: true
			}
		}, {
			path: '/updatepassword',
			name: '修改密码',
			component: () => import('../views/updatepassword.vue')
		}
		
		,{
			path: '/xueshengCenter',
			name: '学生个人中心',
			component: xueshengCenter
		}
		,{
			path: '/jiazhangCenter',
			name: '家长个人中心',
			component: jiazhangCenter
		}
		,{
			path: '/jiaoshiCenter',
			name: '教师个人中心',
			component: jiaoshiCenter
		}
		,{
			path: '/news',
			name: '公告版',
			component: news
		}
		,{
			path: '/jiazhang',
			name: '家长',
			component: jiazhang
		}
		,{
			path: '/xuesheng',
			name: '学生',
			component: xuesheng
		}
		,{
			path: '/examquestion',
			name: '试题管理',
			component: examquestion
		}
		,{
			path: '/jiaoshi',
			name: '教师',
			component: jiaoshi
		}
		,{
			path: '/xueshengchengji',
			name: '学生成绩',
			component: xueshengchengji
		}
		,{
			path: '/exampaper',
			name: '试卷列表',
			component: exampaper
		}
		,{
			path: '/users',
			name: '管理员',
			component: users
		}
		, {
			path: '/exampaperlist',
			name: '考试列表',
			component: exampaperlist
		}, {
			path: '/examrecord',
			name: '考试记录',
			component: examrecord
		}, {
			path: '/examfailrecord',
			name: '错题本',
			component: examfailrecord
		}
		]
	},
	{
		path: '/xueshengRegister',
		name: '学生注册',
		component: xueshengRegister
	},
	{
		path: '/jiazhangRegister',
		name: '家长注册',
		component: jiazhangRegister
	},
	{
		path: '/jiaoshiRegister',
		name: '教师注册',
		component: jiaoshiRegister
	},
	{
		path: '/examination',
		name: '考试',
		component: examination
	},
]

const router = createRouter({
	history: createWebHashHistory(process.env.BASE_URL),
	routes
})

export default router
