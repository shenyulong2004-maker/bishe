<template>
	<div>
		<div class="home_box">
			<!-- 公告版 -->
			<div class="newsList_view">
				<div class="newsList_title">公告版</div>
				<div class="news_list_four">
					<div class="news_left">
						<template v-for="(item,index) in newsList" :key="index">
							<div class="four_left_item animation_box" v-if="index<4" @click="newsDetailClick(item.id)">
								<div class="time_box">
									<div class="date">{{item.addtime.split(' ')[0].split('-')[1]}}/{{item.addtime.split(' ')[0].split('-')[2]}}</div>
									<div class="year">{{item.addtime.split(' ')[0].split('-')[0]}}</div>
								</div>
								<div class="content">
									<div class="news_title">{{item.title}}</div>
									<div class="news_text">{{item.introduction}}</div>
								</div>
							</div>
						</template>
					</div>
					<div class="news_right">
						<template v-for="(item,index) in newsList" :key="index" @click="newsDetailClick(item.id)">
							<div class="four_right_item animation_box" v-if="index>3">
								<div class="img_box">
									<img class="news_img" v-if="isHttp(item.picture)" :src="item.picture.split(',')[0]" alt="">
									<img class="news_img" v-else :src="item.picture?$config.url + item.picture.split(',')[0]:''" alt="">
								</div>
								<div class="content">
									<div class="news_title">{{item.title}}</div>
								</div>
							</div>
						</template>
					</div>
				</div>
				<div class="news_more_view" @click="moreClick('news')">
					<span class="news_more_text">更多</span>
					<el-icon><DArrowRight /></el-icon>
				</div>
			</div>
		</div>
		<formModel ref="newsFormModelRef"></formModel>
	</div>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance
	} from 'vue';
	import {
		useRouter
	} from 'vue-router';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const router = useRouter()
	//公告版弹窗
	import formModel from './news/formModel'
	const newsFormModelRef = ref(null)
	//公告版
	const newsList = ref([])
	const getNewsList = () => {
		context?.$http({
			url: 'news/list',
			method: 'get',
			params:{
				page:1,
				limit: 6
			}
		}).then(res=>{
			newsList.value = res.data.data.list
		})
	}
	const newsDetailClick = (id=null) => {
		if (id){
			newsFormModelRef.value.init(id)
		}
	}
	//判断图片链接是否带http
	const isHttp = (str) => {
        return str && str.substr(0,4)=='http';
    }
	//跳转详情
	const detailClick = (table,id) => {
		router.push(`/index/${table}Detail?id=${id}`)
	}
	const moreClick = (table) => {
		router.push(`/index/${table}List`)
	}
	const init = () => {
		//公告版
		getNewsList()
	}
	init()
</script>

<style lang="scss">
	.home_box {
		border-radius: 4px;
		padding: 0px;
		margin: 0 auto;
		background: none;
		display: flex;
		width: 100%;
		justify-content: space-between;
		align-items: flex-start;
		flex-wrap: wrap;
	}
	
	// 推荐
	.recomList_view {
		padding: 40px calc((100% - 1200px)/2) 120px;
		margin: 0px auto 0;
		background: url(http://clfile.zggen.cn/20240229/af5ddf2a2e984e788174064a392304ca.jpg) no-repeat center bottom,#d9e8ff;
		width: 100%;
		position: relative;
		order: 2;
		.recomList_title {
			padding: 0 0 0 16px;
			margin: 0;
			color: #333;
			background: none;
			font-weight: 600;
			width: 100%;
			font-size: 24px;
			border-color: #666;
			border-width: 0 0 1px;
			line-height: 48px;
			border-style: solid;
			text-align: left;
		}
		// list
		.recommend_list_four {
			padding: 0;
			margin: 40px auto 0;
			display: flex;
			width: calc(100% - 40px);
			justify-content: space-between;
			align-items: flex-start;
			flex-wrap: wrap;
			height: auto;
			
			.recommend_item:nth-of-type(2n - 1) {
				border: 0px solid #eee;
				padding: 0;
				margin: 0;
				overflow: hidden;
				background: #fff;
				width: calc(100% / 4 - 0px);
				position: relative;
				
				.recommend_img_box {
					width: 100%;
					font-size: 0;
					height: 200px;
					.recommend_img {
						object-fit: cover;
						width: 100%;
						height: 100%;
					}
				}
				.recommend_content {
					border: 0px solid #eee;
					box-shadow: 0 0px 0px rgba(0,0,0,.3);
					padding: 20px 0;
					overflow: hidden;
					background: rgba(255,255,255,.9);
					display: flex;
					width: 100%;
					justify-content: center;
					align-items: flex-start;
					flex-wrap: wrap;
					transition: all 0.3s;
					height: 100%;
					.recommend_title1 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title2 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title3 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title4 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title5 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_price {
						color: #f00;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_four_bottom {
						margin: 10px 0 0;
						display: flex;
						justify-content: center;
						align-items: center;
						.recommend_like {
							margin: 0 10px 0 0;
							color: #0266b5;
							display: flex;
							font-size: 14px;
							align-items: center;
							.like_icon {
								margin: 0 4px 0 0;
								color: inherit;
							}
							.like_num {
								color: inherit;
							}
						}
						.recommend_collect {
							margin: 0 10px 0 0;
							color: #ee7810;
							display: flex;
							font-size: 14px;
							align-items: center;
							.el-icon {
								margin: 0 4px 0 0;
								color: inherit;
							}
							.collect_num {
								color: inherit;
							}
						}
						.recommend_clickNum {
							margin: 0 10px 0 0;
							color: #4aac26;
							display: flex;
							font-size: 14px;
							align-items: center;
							.el-icon {
								margin: 0 4px 0 0;
								color: inherit;
							}
							.clickNum_num {
								color: inherit;
							}
						}
					}
				}
			}
			.recommend_item:nth-of-type(2n) {
				border: 0px solid #eee;
				padding: 0;
				margin: 0;
				overflow: hidden;
				background: #fff;
				display: flex;
				width: calc(100% / 4 - 0px);
				position: relative;
				flex-wrap: wrap;
				
				.recommend_img_box {
					width: 100%;
					font-size: 0;
					height: 200px;
					order: 2;
					.recommend_img {
						object-fit: cover;
						width: 100%;
						height: 100%;
					}
				}
				.recommend_content {
					border: 0px solid #eee;
					box-shadow: 0 0px 0px rgba(0,0,0,.3);
					padding: 20px 0;
					background: rgba(255,255,255,.9);
					display: flex;
					width: 100%;
					justify-content: center;
					align-items: flex-start;
					flex-wrap: wrap;
					transition: all 0.3s;
					height: 100%;
					.recommend_title1 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title2 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title3 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title4 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_title5 {
						margin: 0 0 10px;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_price {
						color: #f00;
						width: 100%;
						line-height: 1;
						text-align: center;
					}
					.recommend_four_bottom {
						margin: 10px 0 0;
						display: flex;
						justify-content: center;
						align-items: center;
						.recommend_like {
							margin: 0 10px 0 0;
							color: #0266b5;
							display: flex;
							font-size: 14px;
							align-items: center;
							.like_icon {
								margin: 0 4px 0 0;
								color: inherit;
							}
							.like_num {
								color: inherit;
							}
						}
						.recommend_collect {
							margin: 0 10px 0 0;
							color: #ee7810;
							display: flex;
							font-size: 14px;
							align-items: center;
							.el-icon {
								margin: 0 4px 0 0;
								color: inherit;
							}
							.collect_num {
								color: inherit;
							}
						}
						.recommend_clickNum {
							margin: 0 10px 0 0;
							color: #4aac26;
							display: flex;
							font-size: 14px;
							align-items: center;
							.el-icon {
								margin: 0 4px 0 0;
								color: inherit;
							}
							.clickNum_num {
								color: inherit;
							}
						}
					}
				}
			}
			.recommend_item:nth-of-type(2n - 1):hover {
				cursor: pointer;
				background: #fff;
				
				.recommend_img_box {
					.recommend_img {
					}
				}
				.recommend_content {
					bottom: 0;
					.recommend_title1 {
						color: inherit;
					}
					.recommend_title2 {
						color: inherit;
					}
					.recommend_title3 {
						color: inherit;
					}
					.recommend_title4 {
						color: inherit;
					}
					.recommend_title5 {
						color: inherit;
					}
					.recommend_price {
						color: #f00;
					}
					.recommend_four_bottom {
						.recommend_like {
							color: #0266b5;
							.like_icon {
								color: inherit;
							}
							.like_num {
								color: inherit;
							}
						}
						.recommend_collect {
							color: #ee7810;
							.el-icon {
								color: inherit;
							}
							.collect_num {
								color: inherit;
							}
						}
						.recommend_clickNum {
							color: #4aac26;
							.el-icon {
								color: inherit;
							}
							.clickNum_num {
								color: inherit;
							}
						}
					}
				}
			}
			.recommend_item:nth-of-type(2n):hover {
				cursor: pointer;
				background: #fff;
				
				.recommend_img_box {
					.recommend_img {
					}
				}
				.recommend_content {
					bottom: 0;
					.recommend_title1 {
						color: inherit;
					}
					.recommend_title2 {
						color: inherit;
					}
					.recommend_title3 {
						color: inherit;
					}
					.recommend_title4 {
						color: inherit;
					}
					.recommend_title5 {
						color: inherit;
					}
					.recommend_price {
						color: inherit;
					}
					.recommend_four_bottom {
						color: inherit;
						.recommend_like {
							color: #0266b5;
							.like_icon {
								color: inherit;
							}
							.like_num {
								color: inherit;
							}
						}
						.recommend_collect {
							color: #ee7810;
							.el-icon {
								color: inherit;
							}
							.collect_num {
								color: inherit;
							}
						}
						.recommend_clickNum {
							color: #4aac26;
							.el-icon {
								color: inherit;
							}
							.clickNum_num {
								color: inherit;
							}
						}
					}
				}
			}
		}
		// list
		// animation
		.animation_box {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			z-index: initial;
		}
		.animation_box:hover {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			-webkit-perspective: 1000px;
			perspective: 1000px;
			transition: 0.3s;
		}
		.animation_box img {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			z-index: initial;
		}
		.animation_box img:hover {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			-webkit-perspective: 1000px;
			perspective: 1000px;
			transition: 0.3s;
		}
		// animation
		// 更多
		.recommend_more_view {
			cursor: pointer;
			border: 0px solid #ddd;
			padding: 0;
			margin: 0;
			color: #333;
			line-height: 38px;
			right: calc((100% - 1200px)/2 + 20px);
			border-radius: 0px;
			top: 46px;
			background: none;
			width: 89px;
			position: absolute;
			text-align: right;
			height: 38px;
			.recommend_more_text {
				color: #333;
			}
			.el-icon {
				color: #333;
			}
		}
	}
	// 推荐
	// 新闻资讯
	.newsList_view {
		padding: 40px calc((100% - 1200px)/2)  40px;
		margin: 0 auto;
		background: url(http://clfile.zggen.cn/20240229/35242e5664184ac7a4048c545ad1a089.jpg);
		width: calc(100% - 0px);
		position: relative;
		order: 4;
		height: auto;

		.newsList_title {
			padding: 0 0 0 16px;
			margin: 0 auto 20px;
			color: #333;
			background: none;
			font-weight: 600;
			width: 100%;
			font-size: 24px;
			border-color: #666;
			border-width:  0 0 1px;
			line-height: 48px;
			border-style: solid;
			text-align: left;
		}
		// list
		.news_list_four {
			padding: 0;
			margin: 40px 0 0;
			display: flex;
			width: 100%;
			justify-content: space-between;
			.news_left {
				flex-direction: column;
				display: flex;
				width: 70%;
				justify-content: space-between;
				.four_left_item{
					border: 1px dotted rgba(136, 179, 1,.5);
					border-radius: 8px;
					padding: 20px 20px 30px;
					margin: 0 0 20px;
					background: rgba(255,255,255,.3);
					display: flex;
					width: 100%;
					align-items: center;
					.time_box {
						flex-direction: column;
						display: flex;
						width: 120px;
						align-items: flex-start;
						.year{
							padding: 6px 0 0;
							color: #666;
							line-height: 1;
						}
						.date{
							font-weight: bold;
							font-size: 24px;
							line-height: 1;
						}
					}
				}
				.content{
					margin: 0 0 0 10px;
					width: calc(100% - 130px);
					.news_title{
						overflow: hidden;
						white-space: nowrap;
						font-weight: bold;
						width: 100%;
						text-overflow: ellipsis;
					}
					.news_text{
						padding: 10px 0 0;
						overflow: hidden;
						display: -webkit-box;
						line-height: 24px;
						text-overflow: ellipsis;
						-webkit-box-orient: vertical;
						-webkit-line-clamp: 3;
						height: 72px;
					}
				}
				
			}
			.news_right {
				margin: 0 0 0 20px;
				width: calc(30% - 20px);
				.four_right_item {
					margin: 0 0 20px;
					.img_box {
					width: 100%;
					font-size: 0;
					height: 300px;
						.news_img {
					border-radius: 8px;
					object-fit: cover;
					width: 100%;
					height: 100%;
						}
					}
					.content {
						width: 100%;
						text-align: center;
						.news_title {
							overflow: hidden;
							white-space: nowrap;
							width: 100%;
							font-size: 14px;
							line-height: 24px;
							text-overflow: ellipsis;
						}
					}
				}
			}
		}
		// list
		// animation
		.animation_box {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			z-index: initial;
		}
		.animation_box:hover {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, -5px, 0px);
			-webkit-perspective: 1000px;
			perspective: 1000px;
			transition: 0.3s;
		}
		.animation_box img {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			z-index: initial;
		}
		.animation_box img:hover {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			-webkit-perspective: 1000px;
			perspective: 1000px;
			transition: 0.3s;
		}
		// animation
		// 更多
		.news_more_view {
			cursor: pointer;
			border: 0px solid #ddd;
			padding: 0px 0;
			margin: 0;
			color: #999;
			line-height: 38px;
			right: calc((100% - 1200px)/2 + 20px);
			border-radius: 0px;
			top: 45px;
			background: none;
			width: 89px;
			position: absolute;
			text-align: right;
			height: 38px;
			.news_more_text {
				color: #666;
			}
			.el-icon {
				color: #666;
			}
		}
	}
	// 新闻资讯
	// 首页展示
	.homeList_view {
		padding: 20px calc((100% - 1200px)/2) 40px;
		margin: 0;
		background: #88cddc;
		width: 100%;
		position: relative;
		order: 2;

		.homeList_title {
			padding: 0 0 0 16px;
			margin: 0 0 30px;
			color: #333;
			background: none;
			font-weight: 600;
			width: 100%;
			font-size: 24px;
			border-color: #666;
			border-width: 0 0 1px;
			line-height: 48px;
			border-style: solid;
			text-align: left;
		}
		// list
		.home_list_one {
			padding: 0;
			margin: 20px auto;
			background: none;
			display: flex;
			width: calc(100% - 0px);
			align-items: flex-start;
			flex-wrap: wrap;
			.home_item1 {
				cursor: pointer;
				border: 1px solid #eee;
				padding: 12px 12px 4px;
				margin: 0 20px 20px 0;
				background: #fff;
				width: calc(25% - 20px);
				position: relative;
				.home_img_box {
					margin: 0 0 4px;
					width: 100%;
					.home_img {
						object-fit: cover;
						width: 100%;
						height: 220px;
					}
				}
				.home_content {
					color: #000;
					background: rgba(0,0,0,.0);
					width: 100%;
					line-height: 24px;
					text-align: center;
					.home_title {
						font-size: 14px;
					}
				}
			}
			.home_item2 {
				margin: 0;
				width: 50%;
				.item2_top {
					cursor: pointer;
					border: 1px solid #eee;
					padding: 12px 12px 4px;
					margin: 0px;
					background: #fff;
					width: calc(50% - 10px);
					position: relative;
					float: left;
					.home_img_box {
						margin: 0 0 4px;
						width: 100%;
						.home_img {
							object-fit: cover;
							width: 100%;
							height: 220px;
						}
					}
					.home_content {
						color: #000;
						background: rgba(0,0,0,.0);
						width: 100%;
						line-height: 24px;
						text-align: center;
						.home_title {
							font-size: 14px;
						}
					}
				}
				.item2_bottom {
					cursor: pointer;
					border: 1px solid #eee;
					padding: 12px 12px 4px;
					margin: 0 20px 0 0;
					background: #fff;
					width: calc(50% - 20px);
					position: relative;
					float: right;
					.home_img_box {
						margin: 0 0 4px;
						width: 100%;
						.home_img {
							object-fit: cover;
							width: 100%;
							height: 220px;
						}
					}
					.home_content {
						color: #000;
						background: rgba(0,0,0,.0);
						width: 100%;
						line-height: 24px;
						text-align: center;
						.home_title {
							font-size: 14px;
						}
					}
				}
			}
			.home_item3 {
				cursor: pointer;
				border: 1px solid #ddd;
				padding: 12px 12px 0;
				margin: 0 0px 0 0;
				background: #fff;
				width: calc(25% - 0px);
				position: relative;
				.home_img_box {
					width: 100%;
					font-size: 0;
					.home_img {
						object-fit: cover;
						width: 100%;
						height: 220px;
					}
				}
				.home_content {
					color: #000;
					background: rgba(0,0,0,.0);
					width: 100%;
					line-height: 30px;
					text-align: center;
					.home_title {
						font-size: 14px;
					}
				}
			}
			.home_item4 {
				margin: 0 0 0 0px;
				display: none;
				width: calc(50% - 0px);
				.item4_top {
					cursor: pointer;
					border: 1px solid #ddd;
					padding: 12px 12px 0;
					margin: 0;
					width: calc(50% - 10px);
					position: relative;
					float: left;
					.home_img_box {
						width: 100%;
						.home_img {
							object-fit: cover;
							width: 100%;
							height: 220px;
						}
					}
					.home_content {
						color: #000;
						background: rgba(0,0,0,.0);
						width: 100%;
						line-height: 30px;
						text-align: center;
						.home_title {
							font-size: 14px;
						}
					}
				}
				.item4_bottom {
					cursor: pointer;
					border: 1px solid #ddd;
					padding: 12px 12px 0;
					width: calc(50% - 10px);
					position: relative;
					float: right;
					.home_img_box {
						width: 100%;
						.home_img {
							object-fit: cover;
							width: 100%;
							height: 220px;
						}
					}
					.home_content {
						color: #000;
						background: rgba(0,0,0,.0);
						width: 100%;
						line-height: 30px;
						text-align: center;
						.home_title {
							font-size: 14px;
						}
					}
				}
			}
		}
		// list
		// animation
		.animation_box {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			z-index: initial;
		}
		.animation_box:hover {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, -10px, 0px);
			-webkit-perspective: 1000px;
			perspective: 1000px;
			transition: 0.3s;
		}
		.animation_box img {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			z-index: initial;
		}
		.animation_box img:hover {
			transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			-webkit-perspective: 1000px;
			perspective: 1000px;
			transition: 0.3s;
		}
		// animation
		// 更多
		.homeList_more_view {
			cursor: pointer;
			border: 0px solid #ddd;
			padding: 0;
			margin: 0;
			z-index: 9;
			color: #333;
			line-height: 38px;
			right: calc((100% - 1200px)/2 + 20px);
			border-radius: 0px;
			top: 26px;
			background: none;
			width: 89px;
			position: absolute;
			text-align: right;
			height: 38px;
			.homeList_more_text {
				color: #333;
			}
			.el-icon {
				color: #333;
			}
		}
	}
	// 首页展示
</style>