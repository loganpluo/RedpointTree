# RedpointTree
RedpointTree</br>
## 前言</br>
每个app几乎都有红点业务，RedpointTree组件提供类似android 布局 xml 方式，来简化红点更新逻辑，并同时提供更好的扩展性<br>
![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/0_CrossHierarchyActivity.gif)<br>
<div align=center>CrossHierarchyActivity 效果图</div><br>

![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/0_RedPointTreeInSimpleActivity.gif)<br>
<div align=center>RedPointTreeInSimpleActivity 效果图</div><br>


## 一、红点分布在不同页面的场景（CrossHierarchyActivity xml创建红点树）<br>
### 1、红点树构建<br>

![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/1-create_tree.png)<br>
<div align=center>红点树构建流程图</div><br>

![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/2-%E5%88%B7%E6%96%B0.png)
<div align=center>红点树刷新流程图</div>



#### 代码实现<br>
#### (1)定义xml的红点树<br>

    <!-- messagebox.xml -->
    <RedPointGroup
        xmlns:app="http://schemas.android.com/apk/res-auto"
        app:id="messagebox_root">
        <RedPoint app:id="@string/messagebox_system" app:needCache="true"/>
        <RedPoint app:id="@string/messagebox_moment" app:needCache="true"/>
    </RedPointGroup>

    <!--strings.xml（id定义）-->
    <string name="messagebox_system">messagebox_system</string>
    <string name="messagebox_moment">messagebox_moment</string>

代码说明：<br>
    * RedPointGroup非叶子节点；<br>
    * RedPoint叶子节点；<br>
    * app:id定义id, string类型,<br>
    * app:needCache，是不是缓存unReadCount，注意true时，默认用app:id来当做key，所以app:id定义一定要唯一（用mmkv缓存，构建时候读取缓存，动态观察unReadCount来更新缓存）
    
#### (2)加载xml，构建单利RedpointTree

    RedPointTreeCenter.getInstance().put(this, R.string.messagebox_tree, R.xml.messagebox)
    //如果需要移除则调用RedPointTreeCenter.getInstance().remove("messagebox")

### 2、Redpointview自动关联红点树的节点

![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/3-auto_bind_RedPointTextView.png)<br>
<div align=center>自动关联红点流程图</div>

#### 代码实现(activity_cross_hierarchy.xml)<br>

    <com.github.redpointtree.RedPointTextView
        android:id="@+id/rootRedPoint"
        android:layout_width="20dp"
        android:layout_height="20dp"
        app:layout_constraintTop_toTopOf="@id/rootView"
        app:layout_constraintRight_toRightOf="@id/rootView"
        app:redPointTreeName="@string/messagebox_tree"
        app:redPointId="@string/messagebox_root"
        app:redPointStyle="point"
        android:textColor="@android:color/white"
        android:visibility="invisible"
        tools:visibility="visible"
        tools:text="22"
        android:background="@drawable/red_point"/>

自定义属性说明：<br>
    * app:redPointTreeName 指定 红点树的名字（全局唯一)；<br>
    * app:redPointId  指定节点id（全局唯一)<br>
    * app:redPointStyle 红点样式(红点或者未读数量)<br>

流程过程说明：<br>
    * onAttachedToWindow 自动创建观察者，绑定到对应红点树的节点；<br>
    * onDetachedFromWindow 自动移除观察者<br>
    * 扩展: 如果是app红点view是自定义的view，自定义view可以继承RedPointView，也可以实现来自动绑定观察红点数量<br>


### 3、红点叶子节点支持缓存配置
![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/4-needCache_mmkv.png)<br>
<div align=center>叶子节点缓存流程</div>

#### 代码实现<br>
    <!--messagebox.xml的红点树叶子节点 配置 app:needCache="true"-->
    <RedPoint
        app:id="@string/messagebox_system"
        app:needCache="true"/>

    //缓存prekey配置(为了支持多账号的红点树缓存)
    RedPointConfig.redPointCachePreKey = object:RedPointConfig.IRedPointCachePreKey{
        override fun getRedPointCachePreKey(): String {
            return "1"//do 查询当前登录的userid
        }

    }

    //加载红点树的xml,默认是会load叶子节点的缓存的未读数量(如果叶子节点配置app:needCache="true")
    RedPointTreeCenter.getInstance().put(this, R.string.messagebox_tree, R.xml.messagebox, true)


代码说明：<br>
    * 缓存的key = getRedPointCachePreKey() + "&" + RedPoint.id 所以RedPoint的app:id一定要定义全局唯一(当然如果后面有需要，可以再追加treeName)

### 4、拉取红点未读数量，设置红点数量
#### 4.1 annotation response bean, 自动映射更新到红点view
![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/6-AppRequestFinishListener_clear.png)<br>
<div align=center>post请求消息列表清除红点流程</div>

#### 代码实现<br>
    //step1: app网络层，监听成功回调，类似下面
    HttpUtils.requestFinishListener = object:RequestFinishListener{
        override fun onSuccess(url:String, param: Any, response: Any) {
            ParseRedPointAnnotaionUtil.invalidate(response)//自动解析response bean Annotaion
        }
    }

    //step2:  rsp的响应bean的字段 Annotaion 关联红点树节点； 同时声明刷新模式
    @RedPointCountRsp(treeName = "messagebox",invalidateType = InvalidateType.Tree)
    class MessageBoxUnReadCountRsp(var code:Int = 0,
                                   @BindRedPoint(redPointId = "messagebox_system")
                                   var systemMsgCount:Int = 0,
                                   @BindRedPoint(redPointId = "messagebox_moment")
                                   var momentMsgCount:Int = 0)
属性说明:<br>
    * InvalidateType.Tree : 每个字段解析完后之后，设置@BindRedPoint的未读数量后， 最后统一刷新一下红点树<br>
    * InvalidateType.Point : 每解析完字段，设置@BindRedPoint的未读数量后 都刷新下@BindRedPoint<br>

#### 4.2 手动更新，也是可以

#### 代码实现<br>
        val redpointTree = MessageBoxManager.getInstance(this).redpointTree
        redpointTree.findRedPointById(R.string.messagebox_system)!!.apply {//设置系统消息数量，不需要刷新，因为没有关联红点view刷新
            setUnReadCount(12)
        }

        redpointTree.findRedPointById(R.string.messagebox_moment)!!.apply {//设置动态消息数量，不需要刷新，因为没有关联红点view刷新
            setUnReadCount(1)
        }

        root = redpointTree.findRedPointById("messagebox_root")!!
        root!!.apply {
            addObserver(rootRedPointObserver)
        }.invalidateSelf()//当前activity只有显示root的红点，所以只需要刷新它自己就好



### 5、跳转消息列表，清除对应红点
![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/5-route_clearByIntent.png)<br>
<div align=center>监听全局路由清除红点流程</div>

#### 代码实现<br>
    //step1:app全局路由中添加，RouteListener 类似下面
    //step2:在监听的路由回调中 获取intent，
    //      然后调用RedPointTreeCenter.getInstance().clearByIntent(intent),会自动根据intent来寻找RedPoint，自动清除红点
    RouteUtils.routeListener = object:RouteUtils.RouteListener{
        override fun dispatch(intent: String) {
            RedPointTreeCenter.getInstance().clearByIntent(intent)
        }
    }

    //step3: 在红点树的xml中，对RedPoint 声明 app:clearIntent= "activity跳转的intent"
    <RedPoint
        app:id="@string/messagebox_system"
        app:needCache="true"
        app:clearIntent="redpointtree://system_msglist"/>

    //如果你想写在activity页面，手动清除也可以
    RedPointTreeCenter.getInstance().
        getRedPointTree(R.string.messagebox_tree)?.
        findRedPointById(R.string.messagebox_system).invalidate(0)

### 6、消息列表，下拉刷新清除对应红点

#### 6.1 post请求消息列表，清除对应红点
![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/6-AppRequestFinishListener_clear.png)<br>
<div align=center>post请求消息列表清除红点流程</div>

#### 代码实现<br>
    //step1: app网络层，设置成功回调，类似下面
    HttpUtils.requestFinishListener = object:RequestFinishListener{
        override fun onSuccess(url:String, param: Any, response: Any) {
            ParseRedPointAnnotaionUtil.clear(param)//会判断是不是消息列表第一页拉去, 找到对应RedPoint清除掉
        }
    }

    //step2: post的请求param ，继承 ClearRedPointRequest来说明是不是第一页；
    //       注解@BindRedPoint对应的节点
    @BindRedPoint(treeName = "messagebox", redPointId = "messagebox_moment")
    class MomentMsgListRequest : ClearRedPointRequest {

        var offset = 0

        override fun isFirstPage(): Boolean {
            return offset == 0
        }
    }

##### 拓展获取Retrofit call里面的请求参数<br>
经过研究只能反射获取requestbody,Retrofit并没有公开的api提供
##### 代码实现<br>

    /**
     * 反射获取请求参数的body(Retrofit2.4.0版本)
     */
    fun parseCallRequestParams(call : Call<*>):Array<*>?{
        try{

            call.request().tag()

            //参数返回
            val delegateField = call.javaClass.getDeclaredField("delegate")

            delegateField.isAccessible = true
            val delegate = delegateField.get(call)

            val argsField = delegate.javaClass.getDeclaredField("args")
            if(argsField.type.isArray){
                argsField.isAccessible = true
                return argsField.get(delegate) as Array<*>
            }

        }catch (t:Throwable){
            LogUtil.printStackTrace(t)
        }

        return null
    }

    //例如这个接口
    interface GetMomentMsgListProtocol{

        @Headers("Content-Type: application/json; charset=utf-8")
        @POST("momentlist")
        fun getMomentMsgList(@Body body: MomentMsgListRequest): Call<GetMomentMsgListResult>

    }

    //获取的call
    val call = Retrofit.Builder().build().create(GetMomentMsgListProtocol::class.java).getMomentMsgList(MomentMsgListRequest())
    //通过
    val params = parseCallRequestParams(call)// 数组第一个就是MomentMsgListRequest()

#### 6.2 get请求请求消息列表，清除对应红点
![](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/7-clearByUrl(url)_AppRequestFinishListener.png)<br>
<div align=center>post请求消息列表清除红点流程</div>

#### 代码实现<br>
    //step1: app网络层，监听成功回调，设置clearByUrl， 类似下面
    HttpUtils.requestFinishListener = object:RequestFinishListener{
        override fun onSuccess(url:String, param: Any, response: Any) {
            RedPointTreeCenter.getInstance().clearByUrl(url)//根据url寻找对应的RedPoint，然后清除
        }
    }

    //step2:  在红点树的xml中，对RedPoint 声明 app:clearUrl= "消息列表第一页请求"
    <RedPoint
        app:id="@string/messagebox_system"
        app:needCache="true"
        app:clearUrl="http://SystemMsgListRequest?page=0"/>


#### 6.3 手动代码清除也是可以的
    RedPointTreeCenter.getInstance().
        getRedPointTree(R.string.messagebox_tree)?.
        findRedPointById(R.string.messagebox_system).invalidate(0)


## 二、所有红点在一个界面的场景（RedPointTreeInSimpleActivity 手动创建红点树）

#### 代码实现<br>
    val rootRedPointView = findViewById<View>(R.id.rootRedPoint)
    val root = RedPointGroup("messagebox_root")//构建非叶子节点，id 是string，尽量给唯一值，可以给资源id R.string.messagebox_root
    root.setObserver(object: RedPointObserver {//给非叶子节点添加观察者，通知红点view刷新
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                rootRedPointView.visibility = View.VISIBLE
            }else{
                rootRedPointView.visibility = View.INVISIBLE
            }
        }
    })

    val level11RedPointView = findViewById<View>(R.id.level11RedPoint)
    val level11 = RedPoint("messagebox_system")//构建叶子节点，id 是string，尽量给唯一值，可以给资源id R.string.messagebox_system
    level11.addObserver(object: RedPointObserver {//给叶子节点添加观察者，通知红点view刷新
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                level11RedPointView.visibility = View.VISIBLE
            }else{
                level11RedPointView.visibility = View.INVISIBLE
            }
        }
    })
    level1_1_text.setOnClickListener {//点击叶子节点关联的红点view，清除叶子节点的红点
        level11.invalidate(0)
    }
    level11.setUnReadCount(2)//设置叶子节点的未读数量
    root.addChild(level11)//添加到父节点上


    val level12RedPointView = findViewById<View>(R.id.level12RedPoint)
    val level12 = RedPoint("messagebox_moment")//构建叶子节点，id 是string，尽量给唯一值，可以给资源id R.string.messagebox_moment
    level12.addObserver(object: RedPointObserver {//给叶子节点添加观察者，通知红点view刷新
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                level12RedPointView.visibility = View.VISIBLE
            }else{
                level12RedPointView.visibility = View.INVISIBLE
            }
        }
    })
    level1_2_text.setOnClickListener {//点击叶子节点关联的红点view，清除叶子节点的红点
        level12.invalidate(0)
    }
    level12.setUnReadCount(4)//设置叶子节点的未读数量
    root.addChild(level12)//添加到父节点上

    root.invalidate() //从根节点开始刷新，通知所有节点的observer 刷新红点      
    





 
