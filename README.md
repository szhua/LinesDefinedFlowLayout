# LinesDefinedFlowLayout
一个流式标签，可以自定义行数，进行展开和收起的功能。

项目中用到的一个流式标签，然后自己进行修改。
可以自定义显示的行数。
可以监测行数的变化。
可以监测是否达到最大的行数。

<img src='https://github.com/szhua/LinesDefinedFlowLayout/blob/master/app/1.jpg' width =300px ;><img>

<img src='https://github.com/szhua/LinesDefinedFlowLayout/blob/master/app/2.jpg' width =300px ;><img>

```java
 /**
 * 监测变化
 **/
 if (onLinesChangeListener != null) {
     this.onLinesChangeListener.onlinesChanged(mLineHeight.size());
      }
    if(onLinesUpToMaxListener!=null){
    if(mLineHeight.size()==maxLines){
      onLinesUpToMaxListener.onlinesUptoMax(maxLines);
    }else if(mLineHeight.size()+1==maxLines){
        onLinesUpToMaxListener.onlinesGreaterThanMaxFisrt(maxLines+1);
    }
    }


```
```java
/**
*测量
*/
 @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

         //子view占的实际空间宽度；
        int containerWidth =sizeWidth - getPaddingLeft() - getPaddingRight() ;

        /**
         * 自适应时候的width和height ;
         */
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;
        int lines = 0;
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            //第一个childView，直接测量赋值；
            if(i==0){
                lineHeight =childHeight ;
                lineWidth +=childWidth;
                continue;
            }


            if (lineWidth + childWidth >containerWidth) {
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                if ( lines < maxLines) {
                    height += lineHeight;
                    lines = lines + 1;

                }
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

        }

        this.height = lines;
        setMeasuredDimension(
                //AtMost的处理;自适应的测量;
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()//
        );

    }



```
