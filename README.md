# RedpointTree
RedpointTree</br>
一、所有红点在一个界面的场景（RedPointTreeInSimpleActivity 手动创建红点树）

    val rootRedPointView = findViewById<View>(R.id.rootRedPoint)
    val root = RedPointGroup(1)//构建非叶子节点，id 是int，可以给资源id R.id.root
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
    val level11 = RedPoint(2)//构建叶子节点，id 是int，可以给资源id R.id.system
    level11.setObserver(object: RedPointObserver {//给叶子节点添加观察者，通知红点view刷新
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
    val level12 = RedPoint(3)//构建叶子节点，id 是int，可以给资源id R.id.system
    level12.setObserver(object: RedPointObserver {//给叶子节点添加观察者，通知红点view刷新
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
1、定义xml的红点树
RedPointGroup非叶子节点；
RedPoint叶子节点；
android:id定义id
    
    <RedPointGroup
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/root">
        <RedPoint  android:id="@id/system"/>
        <RedPoint android:id="@id/moment"/>
    </RedPointGroup>

2、 加载xml，构建RedpointTree 和 单利管理的RedpointTree

    class MessageBoxManager(context: Context) {

      val redpointTree: RedpointTree = RedpointTree(context, R.xml.messagebox)

      companion object {
          private var instance:MessageBoxManager? = null

          fun getInstance(context: Context):MessageBoxManager{
              if(instance == null){
                  instance = MessageBoxManager(context)
              }
              return instance!!
          }
      }
    }
    
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
        redpointTree.findRedPointById(R.id.system)!!.apply {//设置系统消息数量，不需要刷新，因为没有关联红点view刷新
            setUnReadCount(12)
        }

        redpointTree.findRedPointById(R.id.moment)!!.apply {//设置动态消息数量，不需要刷新，因为没有关联红点view刷新
            setUnReadCount(1)
        }

        root = redpointTree.findRedPointById(R.id.root)!!
        root!!.apply {
            setObserver(rootRedPointObserver)
        }.invalidateSelf()//当前activity只有显示root的红点，所以只需要刷新它自己就好


    }

    override fun onDestroy() {
        super.onDestroy()
        root!!.removeObserver()//注意移除，因为红点树是单利
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

        val redpointTree = MessageBoxManager.getInstance(this).redpointTree
        systemRedPoint = redpointTree.findRedPointById(R.id.system)

        systemRedPoint!!.apply {//关联系统消息的红点刷新
            setObserver(systemRedPointObserver)
        }.invalidateSelf()//只需要刷新自己

        momentRedPoint = redpointTree.findRedPointById(R.id.moment)!!
        momentRedPoint!!.apply {//关联动态消息的红点刷新
            setObserver(momentRedPointObserver)
        }.invalidateSelf()//只需要刷新自己


    }

    override fun onDestroy() {
        super.onDestroy()

        systemRedPoint!!.removeObserver()//注意移除，因为红点树是单利
        momentRedPoint!!.removeObserver()//注意移除，因为红点树是单利
    }

4、查看系统消息（SystemMsgActivity），清除系统消息的红点

        redpointTree.findRedPointById(R.id.system)!!.invalidate(0)//刷新自己以及递归往上刷新




 
