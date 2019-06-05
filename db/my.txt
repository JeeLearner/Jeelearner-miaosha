1.1springboot引入
1.2集成thymeleaf
1.3集成mybatis
1.4继承redis
1.5返回结果封装

2.1返回结果封装
2.2异常处理
2.3用户登录
	判断用户是否存在
	验证密码
	添加token到cookie
	用户存放到redis
	*请求参数带token允许获取权限
2.4JSR303参数校验

3.1秒杀功能开发
           商品列表页
           商品详情页
           秒杀功能
           订单详情页
4.页面级高并发秒杀优化
优化思路：
    1.页面缓存+URL缓存+对象缓存
    2.页面静态化，前后端分离
    3.静态资源优化
    4.CDN优化
执行优化：
    4.1页面缓存： GoodsController.list
        取缓存
        手动渲染模板
        结果输出
    4.2URL缓存：GoodsController.detail2
    4.3对象缓存： MiaoshaUserService.getById
               MiaoshaUserService.updatePassword
    4.4页面静态化(前后端分离)：
        GoodsController.detail
        resources/static/goods_detail.htm
        OrderController.detail
        resources/static/order_detail.htm
    4.5静态资源优化：
        JS/CSS压缩，减少流量
        多个JS/CSS组合，减少连接数  tengine工具（nginx增强版）
    4.6SDN优化
        CDN就近访问

5.服务级高并发秒杀优化（接口优化）
思路：
    1.redis预减库存较少数据库访问
    2.内存标记减少redis访问
    3.请求先入队缓冲，异步下单，增强用户体验
    4.RabbitMQ
    5.Nginx水平扩展
    6.压测

    减少数据库访问
        思路：同步下单改为异步下单
        1.系统初始化，把商品库存数量加载到redis
            MiaoshaController implements InitializingBean
        2.收到请求，redis预减库存，库存不足，直接返回，否则进入3
            MiaoshaService.miaosha->reduceStock减库存可能失败，改为返回boolean
        3.请求入队，立即返回排队中
            MiaoshaController.miaosha
        4.请求出队，生成订单，减少库存
            MiaoshaMQReceiver.receive
        5.客户端轮询，是否秒杀成功
            resource.static.goods_detail.htm->function getMiaoshaResult
    减少redis网络开销：
        1.内存标记，减少redis访问
            private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

6.安全优化：
    1.秒杀接口地址隐藏
    2.数学公式验证码
    3.接口限流防刷

（1）秒杀接口地址隐藏
    思路：秒杀开始之前，先去请求接口获取秒杀地址
        a.接口改造，带上PathVariable参数
        b.添加生成地址的接口
        c.秒杀收到请求，先生成PathVariable
    实现：MiaoshaController.getMiaoshaPath及do_miaosha
        resource.static.goods_detail.htm->function getMiaoshaPath
（2）数学公式验证码
    思路：点击秒杀之前，先输入验证码，分散用户的请求
        a.添加生成验证码的接口
        b.在获取秒杀路径的时候，验证验证码
        c.ScriptEngine使用
    实现：MiaoshaController.getMiaoshaPath
        VerifyCodeController及VerifyCodeService
        resource.static.goods_detail.htm->function countDown
        刷新验证码：resource.static.goods_detail.htm->function refreshVerifyCode
            注意加参数：+"&timestamp="+new Date().getTime()); 因为客户端有缓存
（3）接口限流防刷
    思路：对接口做限流
        可以用拦截器减少对业务侵入
    实现： MiaoshaController.getMiaoshaPath  @AccessLimit(seconds=5, maxCount=5, needLogin=true)
        com.jeelearn.mymiaosha.access **
        com.jeelearn.mymiaosha.config.WebConfig注入拦截器



解决卖超：
    1.SQL加库存数量判断：防止库存变成负数
        GoodsDao.reduceStock  sql     and stock_count > 0
    2.数据库加唯一索引：防止高并发下用户重复购买
        秒杀时如果同一用户同意时间两个请求，我们是无法判断的，不过可以通过增加数据库唯一索引来避免：
        orderService.createOrder


