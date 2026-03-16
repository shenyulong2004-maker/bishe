import { createRouter, createWebHashHistory } from 'vue-router'
import index from '../views'
import home from '../views/pages/home.vue'
import login from '../views/pages/login.vue'
import xueshengList from '@/views/pages/xuesheng/list'
import xueshengDetail from '@/views/pages/xuesheng/formModel'
import xueshengAdd from '@/views/pages/xuesheng/formAdd'
import jiazhangList from '@/views/pages/jiazhang/list'
import jiazhangDetail from '@/views/pages/jiazhang/formModel'
import jiazhangAdd from '@/views/pages/jiazhang/formAdd'
import newsList from '@/views/pages/news/list'
import jiaoshiList from '@/views/pages/jiaoshi/list'
import jiaoshiDetail from '@/views/pages/jiaoshi/formModel'
import jiaoshiAdd from '@/views/pages/jiaoshi/formAdd'
import xueshengchengjiList from '@/views/pages/xueshengchengji/list'
import xueshengchengjiDetail from '@/views/pages/xueshengchengji/formModel'
import xueshengchengjiAdd from '@/views/pages/xueshengchengji/formAdd'
import exampaperList from '@/views/pages/exam/exampaperList'
import examination from '@/views/pages/exam/examination'
import examrecordList from '@/views/pages/exam/examrecordList'
import examfailrecord from '@/views/pages/exam/examfailrecord'

const routes = [{
		path: '/',
		redirect: '/index/home'
	},
	{
		path: '/index',
		component: index,
		children: [{
			path: 'home',
			component: home
		}
		, {
			path: 'xueshengList',
			component: xueshengList
		}, {
			path: 'xueshengDetail',
			component: xueshengDetail
		}, {
			path: 'xueshengAdd',
			component: xueshengAdd
		}
		, {
			path: 'jiazhangList',
			component: jiazhangList
		}, {
			path: 'jiazhangDetail',
			component: jiazhangDetail
		}, {
			path: 'jiazhangAdd',
			component: jiazhangAdd
		}
		, {
			path: 'newsList',
			component: newsList
		}
		, {
			path: 'jiaoshiList',
			component: jiaoshiList
		}, {
			path: 'jiaoshiDetail',
			component: jiaoshiDetail
		}, {
			path: 'jiaoshiAdd',
			component: jiaoshiAdd
		}
		, {
			path: 'xueshengchengjiList',
			component: xueshengchengjiList
		}, {
			path: 'xueshengchengjiDetail',
			component: xueshengchengjiDetail
		}, {
			path: 'xueshengchengjiAdd',
			component: xueshengchengjiAdd
		}
		, {
			path: 'exampaperList',
			component: exampaperList
		}, {
			path: 'examrecordList',
			component: examrecordList
		}, {
			path: 'examfailrecord',
			component: examfailrecord
		}
		]
	},
	{
		path: '/login',
		component: login
	}
	, {
		path: '/examination',
		component: examination
	}
]

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

export default router
