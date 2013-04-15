/**
 * Copyright 2012
 *
 * Nicolas Desjardins
 * https://github.com/mrKlar
 *
 * Facilite solutions
 * http://www.facilitesolutions.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ca.laplanete.mobile.pageddragdropgrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class DragDropGrid extends ViewGroup implements OnTouchListener, OnLongClickListener {

	private static int ANIMATION_DURATION = 250;
	private static int EGDE_DETECTION_MARGIN = 35;

	private DragDropGridAdapter adapter;
	private OnClickListener onClickListener = null;

	@SuppressLint("UseSparseArrays")
	private SparseArray<Integer> newPositions = new SparseArray<Integer>();

	private int gridWidth = 0;
	private int dragged = -1;
	private int columnWidthSize;
	private int rowHeightSize;
	private int biggestChildWidth;
	private int biggestChildHeight;
	private int computedColumnCount;
	private int computedRowCount;
	private int initialX;
	private int initialY;
	private boolean movingView;
	private int lastTarget = -1;

	private int lastTouchX;
	private int lastTouchY;
	private int gridHeight;
	private DeleteDropZoneView deleteZone;
	private ScrollView container;

	public DragDropGrid(Context context) {
		this(context, null);
	}

	public DragDropGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOnTouchListener(this);
		setOnLongClickListener(this);
		createDeleteZone();
	}
	
	public void setContainer(ScrollView container) {
		this.container = container;
	}

	public void setAdapter(DragDropGridAdapter adapter) {
		this.adapter = adapter;
		addChildViews();
	}

	public void setOnClickListener(OnClickListener l) {
	    onClickListener = l;
	}

	private void addChildViews() {

		for (int item = 0; item < adapter.getItemCount(); item++) {
			addView(adapter.getView(item));
		}
		deleteZone.bringToFront();
	}

	private void animateMoveAllItems() {
		Animation rotateAnimation = createFastRotateAnimation();

		for (int i=0; i < getItemViewCount(); i++) {
			View child = getChildAt(i);
			child.startAnimation(rotateAnimation);
		 }
	}

	private void cancelAnimations() {
		 for (int i=0; i < getItemViewCount(); i++) {
			 if (i != dragged) {
				 View child = getChildAt(i);
				 child.clearAnimation();
			 }
		 }
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
	    return onTouch(null, event);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			touchDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			touchUp(event);
			break;
		}
		if (aViewIsDragged())
			return true;
		return false;
	}

	private void touchUp(MotionEvent event) {
	    if(!aViewIsDragged()) {
	        if(onClickListener != null) {
                View clickedView = getChildAt(getTargetAtCoor((int) event.getX(), (int) event.getY()));
                if(clickedView != null)
                    onClickListener.onClick(clickedView);
            }
	    } else {
    		manageChildrenReordering();
    		hideDeleteView();

    		movingView = false;
    		dragged = -1;
    		lastTarget = -1;
    		enableScroll();
    		cancelAnimations();
	    }
	}
	
	private void enableScroll() {
		if (container != null) {
			container.requestDisallowInterceptTouchEvent(false);
		}
	}
	
	private void disableScroll() {
		if (container != null) {
			container.requestDisallowInterceptTouchEvent(true);
		}
	}

	private void manageChildrenReordering() {
		boolean draggedDeleted = touchUpInDeleteZoneDrop(lastTouchX, lastTouchY);

		if (draggedDeleted) {
			animateDeleteDragged();
			reorderChildrenWhenDraggedIsDeleted();
		} else {
			reorderChildren();
		}
	}

	private void animateDeleteDragged() {
		ScaleAnimation scale = new ScaleAnimation(1.4f, 0f, 1.4f, 0f, biggestChildWidth / 2 , biggestChildHeight / 2);
		scale.setDuration(200);
		scale.setFillAfter(true);
		scale.setFillEnabled(true);

		getChildAt(dragged).clearAnimation();
		getChildAt(dragged).startAnimation(scale);
	}

	private void reorderChildrenWhenDraggedIsDeleted() {
		Integer newDraggedPosition = newPositions.get(dragged,dragged);

		List<View> children = cleanUnorderedChildren();
		addReorderedChildrenToParent(children);

		tellAdapterDraggedIsDeleted(newDraggedPosition);
		removeViewAt(newDraggedPosition);

		requestLayout();
	}

	private void tellAdapterDraggedIsDeleted(Integer newDraggedPosition) {
		adapter.deleteItem(newDraggedPosition);
	}

	private void touchDown(MotionEvent event) {
		initialX = (int)event.getRawX();
		initialY = (int)event.getRawY();

		lastTouchX = (int)event.getRawX() + (gridWidth);
		lastTouchY = (int)event.getRawY();
	}

	private void touchMove(MotionEvent event) {
		if (movingView && aViewIsDragged()) {
			lastTouchX = (int) event.getX();
			lastTouchY = (int) event.getY();

			moveDraggedView(lastTouchX, lastTouchY);
			manageSwapPosition(lastTouchX, lastTouchY);
			manageDeleteZoneHover(lastTouchX, lastTouchY);
		}
	}

	private void manageDeleteZoneHover(int x, int y) {
		Rect zone = new Rect();
		deleteZone.getHitRect(zone);

		if (zone.intersect(x, y, x+1, y+1)) {
			deleteZone.highlight();
		} else {
			deleteZone.smother();
		}
	}

	private boolean touchUpInDeleteZoneDrop(int x, int y) {
		return false;
	}

	private void moveDraggedView(int x, int y) {
		View childAt = getChildAt(dragged);
		int width = childAt.getMeasuredWidth();
		int height = childAt.getMeasuredHeight();

		int l = x - (1 * width / 2);
		int t = y - (1 * height / 2);

		childAt.layout(l, t, l + width, t + height);
	}

	private void manageSwapPosition(int x, int y) {
		int target = getTargetAtCoor(x, y);
		if (childHasMoved(target) && target != lastTarget) {
			animateGap(target);
			lastTarget = target;
		}
	}

	private void removeItemChildren(List<View> children) {
		for (View child : children) {
			removeView(child);
		}
	}

	private boolean onLeftEdgeOfScreen(int x) {

		int leftEdgeXCoor = gridWidth;
		int distanceFromEdge = x - leftEdgeXCoor;
		return (x > 0 && distanceFromEdge <= EGDE_DETECTION_MARGIN);
	}

	private boolean onRightEdgeOfScreen(int x) {

		int rightEdgeXCoor = (gridWidth) + gridWidth;
		int distanceFromEdge = rightEdgeXCoor - x;
		return (x > (rightEdgeXCoor - EGDE_DETECTION_MARGIN)) && (distanceFromEdge < EGDE_DETECTION_MARGIN);
	}

	private void animateGap(int targetLocationInGrid) {
		int viewAtPosition = currentViewAtPosition(targetLocationInGrid);

		if (viewAtPosition == dragged) {
			return;
		}

		View targetView = getChildAt(viewAtPosition);

		Point oldXY = getCoorForIndex(viewAtPosition);
		Point newXY = getCoorForIndex(newPositions.get(dragged, dragged));

		Point oldOffset = computeTranslationStartDeltaRelativeToRealViewPosition(targetLocationInGrid, viewAtPosition, oldXY);
		Point newOffset = computeTranslationEndDeltaRelativeToRealViewPosition(oldXY, newXY);

		animateMoveToNewPosition(targetView, oldOffset, newOffset);
		saveNewPositions(targetLocationInGrid, viewAtPosition);
	}

	private Point computeTranslationEndDeltaRelativeToRealViewPosition(Point oldXY, Point newXY) {
		return new Point(newXY.x - oldXY.x, newXY.y - oldXY.y);
	}

	private Point computeTranslationStartDeltaRelativeToRealViewPosition(int targetLocation, int viewAtPosition, Point oldXY) {
		Point oldOffset;
		if (viewWasAlreadyMoved(targetLocation, viewAtPosition)) {
			Point targetLocationPoint = getCoorForIndex(targetLocation);
			oldOffset = computeTranslationEndDeltaRelativeToRealViewPosition(oldXY, targetLocationPoint);
		} else {
			oldOffset = new Point(0,0);
		}
		return oldOffset;
	}

	private void saveNewPositions(int targetLocation, int viewAtPosition) {
		newPositions.put(viewAtPosition, newPositions.get(dragged, dragged));
		newPositions.put(dragged, targetLocation);
		tellAdapterToSwapDraggedWithTarget(newPositions.get(dragged, dragged), newPositions.get(viewAtPosition, viewAtPosition));
	}

	private boolean viewWasAlreadyMoved(int targetLocation, int viewAtPosition) {
		return viewAtPosition != targetLocation;
	}

	private void animateMoveToNewPosition(View targetView, Point oldOffset, Point newOffset) {
		AnimationSet set = new AnimationSet(true);

		Animation rotate = createFastRotateAnimation();
		Animation translate = createTranslateAnimation(oldOffset, newOffset);

		set.addAnimation(rotate);
		set.addAnimation(translate);

		targetView.clearAnimation();
		targetView.startAnimation(set);
	}

	private TranslateAnimation createTranslateAnimation(Point oldOffset, Point newOffset) {
		TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
															  Animation.ABSOLUTE, newOffset.x,
															  Animation.ABSOLUTE, oldOffset.y,
															  Animation.ABSOLUTE, newOffset.y);
		translate.setDuration(ANIMATION_DURATION);
		translate.setFillEnabled(true);
		translate.setFillAfter(true);
		translate.setInterpolator(new AccelerateDecelerateInterpolator());
		return translate;
	}

	private Animation createFastRotateAnimation() {
		Animation rotate = new RotateAnimation(
			0.0f,
			0.0f,
			Animation.RELATIVE_TO_SELF,
			0.5f,
			Animation.RELATIVE_TO_SELF,
			0.5f
		);

	 	rotate.setRepeatMode(Animation.REVERSE);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setDuration(60);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());

		return rotate;
	}

	private int currentViewAtPosition(int targetLocation) {
		int viewAtPosition = targetLocation;
		for (int i = 0; i < newPositions.size(); i++) {
			int value = newPositions.valueAt(i);
			if (value == targetLocation) {
				viewAtPosition = newPositions.keyAt(i);
				break;
			}
		}
		return viewAtPosition;
	}

	private Point getCoorForIndex(int index) {

		int row = index / computedColumnCount;
		int col = index - (row * computedColumnCount);

		int x = (gridWidth) + (columnWidthSize * col);
		int y = rowHeightSize * row;

		return new Point(x, y);
	}

	private int getTargetAtCoor(int x, int y) {
		int col = getColumnOfCoordinate(x);
		int row = getRowOfCoordinate(y);
		int position = col + (row * computedColumnCount);

		return position;
	}

	private int getColumnOfCoordinate(int x) {
		int col = 0;
		for (int i = 1; i <= computedColumnCount; i++) {
			int colRightBorder = (i * columnWidthSize);
			if (x < colRightBorder) {
				break;
			}
			col++;
		}
		return col;
	}

	private int getRowOfCoordinate(int y) {
		int row = 0;
		for (int i = 1; i <= computedRowCount; i++) {
			if (y < i * rowHeightSize) {
				break;
			}
			row++;
		}
		return row;
	}

	private void reorderChildren() {
		List<View> children = cleanUnorderedChildren();
		addReorderedChildrenToParent(children);
		requestLayout();
	}

	private List<View> cleanUnorderedChildren() {
		List<View> children = saveChildren();
		removeItemChildren(children);
		return children;
	}

	private void addReorderedChildrenToParent(List<View> children) {
		List<View> reorderedViews = reeorderView(children);
		newPositions.clear();

		for (View view : reorderedViews) {
			if (view != null)
				addView(view);
		}

		deleteZone.bringToFront();
	}

	private List<View> saveChildren() {
		List<View> children = new ArrayList<View>();
		for (int i = 0; i < getItemViewCount(); i++) {
			View child = getChildAt(i);
			child.clearAnimation();
			children.add(child);
		}
		return children;
	}

	private List<View> reeorderView(List<View> children) {
		View[] views = new View[children.size()];

		for (int i = 0; i < children.size(); i++) {
			int position = newPositions.get(i, -1);
			if (childHasMoved(position)) {
				views[position] = children.get(i);
			} else {
				views[i] = children.get(i);
			}
		}
		return new ArrayList<View>(Arrays.asList(views));
	}

	private boolean childHasMoved(int position) {
		return position != -1;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

		Display display = wm.getDefaultDisplay();

		widthSize = acknowledgeWidthSize(widthMode, widthSize, display);
		heightSize = acknowledgeHeightSize(heightMode, heightSize, display);

		adaptChildrenMeasuresToViewSize(widthSize, heightSize);
		searchBiggestChildMeasures();
		computeGridMatrixSize(widthSize, heightSize);
		computeColumnsAndRowsSizes(widthSize, heightSize);

		measureChild(deleteZone, MeasureSpec.makeMeasureSpec(gridWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int)getPixelFromDip(40), MeasureSpec.EXACTLY));

		setMeasuredDimension(widthSize, heightSize);
	}

	private float getPixelFromDip(int size) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics());
		return px;
	}

	private void computeColumnsAndRowsSizes(int widthSize, int heightSize) {
		columnWidthSize = widthSize / computedColumnCount;
		rowHeightSize = heightSize / computedRowCount;
	}

	private void computeGridMatrixSize(int widthSize, int heightSize) {
		if (adapter.getColumnCount() != -1 && adapter.getRowCount() != -1) {
			computedColumnCount = adapter.getColumnCount();
			computedRowCount = adapter.getRowCount();
		} else {
			if (biggestChildWidth > 0 && biggestChildHeight > 0) {
				computedColumnCount = widthSize / biggestChildWidth;
				computedRowCount = heightSize / biggestChildHeight;
			}
		}

		if (computedColumnCount == 0) {
			computedColumnCount = 1;
		}

		if (computedRowCount == 0) {
			computedRowCount = 1;
		}
	}

	private void searchBiggestChildMeasures() {
		biggestChildWidth = 0;
		biggestChildHeight = 0;
		for (int index = 0; index < getItemViewCount(); index++) {
			View child = getChildAt(index);

			if (biggestChildHeight < child.getMeasuredHeight()) {
				biggestChildHeight = child.getMeasuredHeight();
			}

			if (biggestChildWidth < child.getMeasuredWidth()) {
				biggestChildWidth = child.getMeasuredWidth();
			}
		}
	}

	private int getItemViewCount() {
		// -1 to remove the DeleteZone from the loop
		return getChildCount()-1;
	}

	private void adaptChildrenMeasuresToViewSize(int widthSize, int heightSize) {
		if (adapter.getColumnCount() != DragDropGridAdapter.AUTOMATIC && adapter.getRowCount() != DragDropGridAdapter.AUTOMATIC) {
			int desiredGridItemWidth = widthSize / adapter.getColumnCount();
			int desiredGridItemHeight = heightSize / adapter.getRowCount();
			measureChildren(MeasureSpec.makeMeasureSpec(desiredGridItemWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(desiredGridItemHeight, MeasureSpec.AT_MOST));
		} else {
			measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		}
	}

	private int acknowledgeHeightSize(int heightMode, int heightSize, Display display) {
		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = display.getHeight();
		}
		gridHeight = heightSize;
		return heightSize;
	}

	private int acknowledgeWidthSize(int widthMode, int widthSize, Display display) {
		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = display.getWidth();
		}
		gridWidth = widthSize;
		return widthSize;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = (l + r);
		
		int col = 0;
		int row = 0;
		for (int childIndex = 0; childIndex < adapter.getItemCount(); childIndex++) {
			layoutAChild(width, col, row, childIndex);
			col++;
			if (col == computedColumnCount) {
				col = 0;
				row++;
			}
		}
	}

	private void layoutAChild(int width, int col, int row, int childIndex) {

		View child = getChildAt(childIndex);

		int left = 0;
		int top = 0;
		if (childIndex == dragged && lastTouchOnEdge()) {
			left = computeEdgeXCoor(child);
			top = lastTouchY - (child.getMeasuredHeight() / 2);
		} else {
			left = (col * columnWidthSize) + ((columnWidthSize - child.getMeasuredWidth()) / 2);
			top = (row * rowHeightSize) + ((rowHeightSize - child.getMeasuredHeight()) / 2);
		}
		child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
	}

	private boolean lastTouchOnEdge() {
		return onRightEdgeOfScreen(lastTouchX) || onLeftEdgeOfScreen(lastTouchX);
	}

	private int computeEdgeXCoor(View child) {
		int left;
		left = lastTouchX - (child.getMeasuredWidth() / 2);
		if (onRightEdgeOfScreen(lastTouchX)) {
			left = left - gridWidth;
		} else if (onLeftEdgeOfScreen(lastTouchX)) {
			left = left + gridWidth;
		}
		return left;
	}

	@Override
	public boolean onLongClick(View v) {	    
	    if(positionForView(v) != -1) {
	    	disableScroll();
	    	
    		movingView = true;
    		dragged = positionForView(v);
    
    		animateMoveAllItems();
    
    		animateDragged();
    		popDeleteView();
    
    		return true;
	    }
	    
	    return false;
	}

	private void animateDragged() {

		ScaleAnimation scale = new ScaleAnimation(1f, 1.4f, 1f, 1.4f, biggestChildWidth / 2 , biggestChildHeight / 2);
		scale.setDuration(200);
		scale.setFillAfter(true);
		scale.setFillEnabled(true);

		if (aViewIsDragged()) {
			getChildAt(dragged).clearAnimation();
			getChildAt(dragged).startAnimation(scale);
		}
	}

	private boolean aViewIsDragged() {
		return dragged != -1;
	}

	private void popDeleteView() {

		deleteZone.setVisibility(View.GONE);

		int l = deleteZone.getMeasuredWidth();
		int t = gridHeight - deleteZone.getMeasuredHeight();
		deleteZone.layout(l,  t, l + gridWidth, t + gridHeight);
	}

	private void createDeleteZone() {
		deleteZone = new DeleteDropZoneView(getContext());
		deleteZone.setVisibility(View.GONE);
		addView(deleteZone);
	}

	private void hideDeleteView() {
		deleteZone.setVisibility(View.GONE);
	}

	private int positionForView(View v) {
		for (int index = 0; index < getItemViewCount(); index++) {
			View child = getChildAt(index);
				if (isPointInsideView(initialX, initialY, child)) {
					return index;
				}
		}
		return -1;
	}

	private boolean isPointInsideView(float x, float y, View view) {
		int location[] = new int[2];
		view.getLocationOnScreen(location);
		int viewX = location[0];
		int viewY = location[1];

		if (pointIsInsideViewBounds(x, y, view, viewX, viewY)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean pointIsInsideViewBounds(float x, float y, View view, int viewX, int viewY) {
		return (x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight()));
	}

	private void tellAdapterToSwapDraggedWithTarget(int dragged, int target) {
		adapter.swapItems(dragged, target);
	}
}
