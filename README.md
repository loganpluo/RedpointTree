# RedpointTree
RedpointTree</br>
前言</br>

一、红点分布在不同页面的场景（CrossHierarchyActivity xml创建红点树）<br>
1、红点树构建<br>

![这里随便写文字](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/1-create_tree.png)<br>
                                        < center>构建流程图< /center>

![这里随便写文字](https://github.com/loganpluo/RedpointTree/blob/master/redpointtree/pic/2-%E5%88%B7%E6%96%B0.png)<br>
                                     < /center>红点树刷新流程图< /center>

代码实现
(1)定义xml的红点树<br>

    messagebox.xml
    <RedPointGroup
        xmlns:app="http://schemas.android.com/apk/res-auto"
        app:id="messagebox_root">
        <RedPoint app:id="@string/messagebox_system" app:needCache="true"/>
        <RedPoint app:id="@string/messagebox_moment" app:needCache="true"/>
    </RedPointGroup>

    strings.xml（id定义）
    <string name="messagebox_system">messagebox_system</string>
    <string name="messagebox_moment">messagebox_moment</string>

RedPointGroup非叶子节点；<br>
RedPoint叶子节点；<br>
app:id定义id, string类型,<br>
app:needCache，是不是缓存unReadCount，注意true时，默认用app:id来当做key，所以app:id定义一定要唯一
（用mmkv缓存，构建时候读取缓存，动态观察unReadCount来更新缓存）
    
（2) 加载xml，构建单利RedpointTree

    RedPointTreeCenter.getInstance().put(this, R.string.messagebox_tree, R.xml.messagebox)
    //如果需要移除则调用RedPointTreeCenter.getInstance().remove("messagebox")

    
3、初始化红点树（未读数 和 关联刷新红点）（CrossHierarchyActivity）

    3.1 activity_cross_hierarchy.xml 
    使用能自定义关联红点树中的节点的 RedPointTextView(onAttachedToWindow 和 onDetachedFromWindow 自动关联红点树的节点)
    app:redPointTreeName指定 红点树的名字；
    app:redPointId 指定节点id
    app:redPointStyle 红点样式(红点或者未读数量)
    如果app里面使用自定义view，可以继承RedPointView 来自动绑定观察红点数量
    
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
    
    3.2 设置红点未读数量
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


5、点击进入消息盒子（MessageBoxActivity）

    activity_messagebox.xml 使用自定义RedPointTextView自动关联红点节点
    <?xml version="1.0" encoding="utf-8"?>
    <android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        xmlns:tools="http://schemas.android.com/tools"
        android:padding="10dp">

        <TextView
            android:id="@+id/systemRedPointText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="5dp"
            android:text="click_system"
            app:layout_constraintTop_toBottomOf="@id/rootView"/>

        <com.github.redpointtree.RedPointTextView
            android:id="@+id/systemRedPointView"
            android:layout_width="5dp"
            android:layout_height="5dp"
            app:layout_constraintTop_toTopOf="@id/systemRedPointText"
            app:layout_constraintRight_toRightOf="@id/systemRedPointText"
            app:redPointTreeName="@string/messagebox_tree"
            app:redPointId="@string/messagebox_system"
            app:redPointStyle="point"
            android:textColor="@android:color/white"
            android:visibility="invisible"
            tools:visibility="visible"
            android:background="@drawable/red_point"/>

        <TextView
            android:id="@+id/momentRedPointText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="5dp"
            android:text="click_moment"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toBottomOf="@id/rootView"/>


        <com.github.redpointtree.RedPointTextView
            android:id="@+id/momentRedPointView"
            android:layout_width="5dp"
            android:layout_height="5dp"
            app:layout_constraintTop_toTopOf="@id/momentRedPointText"
            app:layout_constraintRight_toRightOf="@id/momentRedPointText"
            app:redPointTreeName="@string/messagebox_tree"
            app:redPointId="@string/messagebox_moment"
            app:redPointStyle="point"
            android:textColor="@android:color/white"
            android:visibility="invisible"
            tools:visibility="visible"
            android:background="@drawable/red_point"/>

    </android.support.constraint.ConstraintLayout>



4、查看系统消息（SystemMsgActivity），清除系统消息的红点

       val redpointTree = RedPointTreeCenter.getInstance().getRedPointTree(getString(R.string.messagebox_tree))

       redpointTree!!.findRedPointById(R.string.messagebox_system)!!.invalidate(0)
       //通常还需要拉去消息列表第一页成功后，invalidate(0) (防止用户停留在这个页面，下拉刷新)


二、所有红点在一个界面的场景（RedPointTreeInSimpleActivity 手动创建红点树）

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
    





 
