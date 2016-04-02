# PullZoomView
###类似QQ空间，新浪微博个人主页下拉头部放大的布局效果，支持ListView，GridView，ScrollView，WebView，RecyclerView，以及其他的任意View和ViewGroup。支持头部视差动画和阻尼下拉放大。

欢迎大家下载体验本项目，如果使用过程中遇到什么问题，欢迎反馈。

## 演示
 ![image](https://github.com/jeasonlzy0216/PullZoomView/blob/master/screenshots/demo1.png)![image](https://github.com/jeasonlzy0216/PullZoomView/blob/master/screenshots/demo2.png)![image](https://github.com/jeasonlzy0216/PullZoomView/blob/master/screenshots/demo3.png)![image](https://github.com/jeasonlzy0216/PullZoomView/blob/master/screenshots/demo4.gif)![image](https://github.com/jeasonlzy0216/PullZoomView/blob/master/screenshots/demo5.gif)![image](https://github.com/jeasonlzy0216/PullZoomView/blob/master/screenshots/demo6.gif)

## 1.温馨提示
  该项目和我github上其他的view相关的项目已经一起打包上传到jCenter仓库中（源码地址 [https://github.com/jeasonlzy0216/ViewCore](https://github.com/jeasonlzy0216/ViewCore) ），使用的时候可以直接使用compile依赖，用法如下
```java
	compile 'com.lzy.widget:view-core:0.1.5'
```
或者使用
```java
    compile project(':pullzoom')
```


## 2.实现原理 
PullZoomView 继承至 ScrollView，通过布局设置 Tag 和 重写滑动事件，达到 PullZoomView 与 其他子View嵌套使用，同时增加了外部监听器。

## 3.自定义属性
<table>
  <tdead>
    <tr>
      <th align="center">自定义属性名字</th>
      <th align="center">参数含义</th>
    </tr>
  </tdead>
  <tbody>
    <tr>
      <td align="center">pzv_sensitive</td>
      <td align="center">图片放大效果相对于手指滑动距离的敏感度，越小越敏感，默认值 1.5</td>
    </tr>
    <tr>
      <td align="center">pzv_isZoomEnable</td>
      <td align="center">是否允许下拉时头部放大效果，默认 true，即为允许</td>
    </tr>
    <tr>
      <td align="center">pzv_isParallax</td>
      <td align="center">滑动时，是否头部具有视差动画，默认 true， 即为有</td>
    </tr>
    <tr>
      <td align="center">pzv_zoomTime</td>
      <td align="center">松手时，缩放头部还原到原始大小的时间，单位毫秒，默认 500毫秒</td>
    </tr>
  </tbody>
</table>

## 4.使用注意事项
 * `PullZoomView` 使用时，需要三个Tag同时设置才能正常工作，否者会抛出异常，即在xml布局中对应的View中，加入如下tag，详细请参看Demo。
```java
    android:tag="header"
	android:tag="zoom"
	android:tag="content"
```
 * 与ListView嵌套使用时，ListView需要使用本库中提供的 `ExpandListView`
 * 与GridView嵌套使用时，GridView需要使用本库中提供的 `ExpandGridView`
 * 与RecyclerView嵌套使用时，RecyclerView使用v7包中原生的即可，但是LayoutManager需要使用本库中提供的三个管理者，分别是 `ExpandLinearLayoutManager`, `ExpandGridLayoutManager`, `ExpandStaggeredGridLayoutManager`
 * 其他例如 ScrollView，WebView，View子类，ViewGroup子类均使用原生类即可，不用做任何改动。

## 3.代码参考
该类`PullZoomView`对外提供了滑动监听器，三个方法分别表示

 * `onScroll(int l, int t, int oldl, int oldt)`: 表示`PullZoomView`滑动全程的监听
 * `onHeaderScroll(int currentY, int maxY)`: 表示头部从完全展现，到完全滑出的监听过程
 * `onContentScroll(int l, int t, int oldl, int oldt)`： 表示除了头部外，内容布局从最顶部滑动到最底部的监听过程

此外允许通过代码对滑动行为动态控制
```java
	pzv.setIsParallax(true);    //允许视差动画
    pzv.setIsZoomEnable(true);  //允许头部放大
    pzv.setSensitive(1.5f);     //敏感度1.5
    pzv.setZoomTime(500);       //头部缩放时间500毫秒
```
以上四个方法不是必须设置的，根据需要，可以使用自定义属性设置，也可以使用以上代码动态设置。

完整代码参考如下：
```java
	PullZoomView pzv = (PullZoomView) findViewById(R.id.pzv);
        pzv.setIsParallax(true);    //允许视差动画
        pzv.setIsZoomEnable(true);  //允许头部放大
        pzv.setSensitive(1.5f);     //敏感度1.5
        pzv.setZoomTime(500);       //头部缩放时间500毫秒
        pzv.setOnScrollListener(new PullZoomView.OnScrollListener() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                System.out.println("onScroll   t:" + t + "  oldt:" + oldt);
            }

            @Override
            public void onHeaderScroll(int currentY, int maxY) {
                System.out.println("onHeaderScroll   currentY:" + currentY + "  maxY:" + maxY);
            }

            @Override
            public void onContentScroll(int l, int t, int oldl, int oldt) {
                System.out.println("onContentScroll   t:" + t + "  oldt:" + oldt);
            }
        });
```