# RedpointTree
RedpointTree</br>
一、所有红点在一个界面的场景（RedPointTreeInSimpleActivity 手动创建红点树）

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
    
二、红点分布在不同页面的场景（CrossHierarchyActivity xml创建红点树）<br>
1、定义xml的红点树<br>
RedPointGroup非叶子节点；<br>
RedPoint叶子节点；<br>
app:id定义id, string类型,<br>
app:needCache，是不是缓存unReadCount，注意true时，默认用app:id来当做key，所以app:id定义一定要唯一
（用mmkv缓存，构建时候读取缓存，动态观察unReadCount来更新缓存）
    
    strings.xml
    <string name="messagebox_system">messagebox_system</string>
    <string name="messagebox_moment">messagebox_moment</string>
    
    messagebox.xml
    <RedPointGroup
        xmlns:app="http://schemas.android.com/apk/res-auto"
        app:id="messagebox_root">
        <RedPoint app:id="@string/messagebox_system" app:needCache="true"/>
        <RedPoint app:id="@string/messagebox_moment" app:needCache="true"/>
    </RedPointGroup>

2、 加载xml，构建单利RedpointTree

    RedPointTreeCenter.getInstance().put(this, "messagebox", R.xml.messagebox)
    //如果需要移除则调用RedPointTreeCenter.getInstance().remove("messagebox")

    
3、初始化红点树（未读数 和 关联刷新红点）（CrossHierarchyActivity）

    private val rootRedPointObserver = object:RedPointObserver{//刷新根节点的红点的观察者
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                rootRedPoint.visibility = View.VISIBLE
            }else{
                rootRedPoint.visibility = View.INVISIBLE
            }
        }
    }

    private var root: RedPoint? = null

    private fun loadMessageBoxTree(){

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
        
        //如果时监听广播设置红点数量之后，调用root!!.invalidate()可以刷新整个树

    }

    override fun onDestroy() {
        super.onDestroy()
        root!!.removeObserver(rootRedPointObserver)//注意移除，因为红点树是单利
    }

5、点击进入消息盒子（MessageBoxActivity）

    private val systemRedPointObserver = object:RedPointObserver{//系统消息的红点刷新的观察者
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                systemRedPointView.visibility = View.VISIBLE
            }else{
                systemRedPointView.visibility = View.INVISIBLE
            }
        }

    }

    private val momentRedPointObserver = object:RedPointObserver{//动态消息的红点刷新的观察者
        override fun notify(unReadCount: Int) {
            if(unReadCount > 0){
                momentRedPointView.visibility = View.VISIBLE
            }else{
                momentRedPointView.visibility = View.INVISIBLE
            }
        }

    }

    private var systemRedPoint: RedPoint? = null
    private var momentRedPoint: RedPoint? = null

    private fun loadMessageBoxTree(){

        val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree("messagebox")
        systemRedPoint = redpointTree.findRedPointById(R.string.messagebox_system)

        systemRedPoint!!.apply {//关联系统消息的红点刷新
            addObserver(systemRedPointObserver)
        }.invalidateSelf()//只需要刷新自己

        momentRedPoint = redpointTree.findRedPointById(R.string.messagebox_moment)!!
        momentRedPoint!!.apply {//关联动态消息的红点刷新
            addObserver(momentRedPointObserver)
        }.invalidateSelf()//只需要刷新自己


    }

    override fun onDestroy() {
        super.onDestroy()

        systemRedPoint!!.removeObserver(systemRedPointObserver)//注意移除，因为红点树是单利
        momentRedPoint!!.removeObserver(momentRedPointObserver)//注意移除，因为红点树是单利
    }

4、查看系统消息（SystemMsgActivity），清除系统消息的红点

        redpointTree.findRedPointById(R.string.messagebox_system)!!.invalidate(0)//刷新自己以及递归往上刷新




 
