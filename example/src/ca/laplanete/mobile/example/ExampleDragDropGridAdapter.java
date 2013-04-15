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
package ca.laplanete.mobile.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ca.laplanete.mobile.pageddragdropgrid.DragDropGrid;
import ca.laplanete.mobile.pageddragdropgrid.DragDropGridAdapter;
import ca.laplanete.mobile.pageddragdropgrid.DragDropItem;

public class ExampleDragDropGridAdapter implements DragDropGridAdapter {

	private Context context;
	private DragDropGrid gridview;
	private List<DragDropItem> items = new ArrayList<DragDropItem>();
	
	public ExampleDragDropGridAdapter(Context context, DragDropGrid gridview) {
		super();
		this.context = context;
		this.gridview = gridview;
		
		DragDropItem firstItem = new DragDropItem(10, "Item 10", R.drawable.ic_launcher);
		firstItem.setIsMoveable(false);
		items.add(firstItem);
		items.add(new DragDropItem(11, "Item 11", R.drawable.ic_launcher));
		items.add(new DragDropItem(12, "Item 12",R.drawable.ic_launcher));
		items.add(new DragDropItem(13, "Item 13",R.drawable.ic_launcher));
		items.add(new DragDropItem(14, "Item 14",R.drawable.ic_launcher));
		items.add(new DragDropItem(15, "Item 15",R.drawable.ic_launcher));
		items.add(new DragDropItem(16, "Item 16",R.drawable.ic_launcher));
		items.add(new DragDropItem(17, "Item 17",R.drawable.ic_launcher));
		items.add(new DragDropItem(18, "Item 18",R.drawable.ic_launcher));
		items.add(new DragDropItem(19, "Item 19",R.drawable.ic_launcher));
		items.add(new DragDropItem(20, "Item 20",R.drawable.ic_launcher));
		items.add(new DragDropItem(21, "Item 21",R.drawable.ic_launcher));
		items.add(new DragDropItem(22, "Item 22",R.drawable.ic_launcher));
		items.add(new DragDropItem(23, "Item 23",R.drawable.ic_launcher));
		items.add(new DragDropItem(24, "Item 24",R.drawable.ic_launcher));
		items.add(new DragDropItem(25, "Item 25", R.drawable.ic_launcher));
		items.add(new DragDropItem(26, "Item 26", R.drawable.ic_launcher));
		items.add(new DragDropItem(27, "Item 27",R.drawable.ic_launcher));
		items.add(new DragDropItem(28, "Item 28",R.drawable.ic_launcher));
		items.add(new DragDropItem(29, "Item 29",R.drawable.ic_launcher));
		items.add(new DragDropItem(30, "Item 30",R.drawable.ic_launcher));
		items.add(new DragDropItem(31, "Item 31",R.drawable.ic_launcher));
		items.add(new DragDropItem(32, "Item 32",R.drawable.ic_launcher));
		items.add(new DragDropItem(33, "Item 33",R.drawable.ic_launcher));
		items.add(new DragDropItem(34, "Item 34",R.drawable.ic_launcher));
		items.add(new DragDropItem(35, "Item 35",R.drawable.ic_launcher));
		items.add(new DragDropItem(36, "Item 36",R.drawable.ic_launcher));
		items.add(new DragDropItem(37, "Item 37",R.drawable.ic_launcher));
		items.add(new DragDropItem(38, "Item 38",R.drawable.ic_launcher));
		items.add(new DragDropItem(39, "Item 39",R.drawable.ic_launcher));
		items.add(new DragDropItem(40, "Item 40",R.drawable.ic_launcher));
		items.add(new DragDropItem(41, "Item 41",R.drawable.ic_launcher));
		items.add(new DragDropItem(42, "Item 42",R.drawable.ic_launcher));
		items.add(new DragDropItem(43, "Item 43",R.drawable.ic_launcher));
		items.add(new DragDropItem(44, "Item 44",R.drawable.ic_launcher));
		items.add(new DragDropItem(45, "Item 45",R.drawable.ic_launcher));
		items.add(new DragDropItem(46, "Item 46",R.drawable.ic_launcher));
		items.add(new DragDropItem(47, "Item 47",R.drawable.ic_launcher));
		items.add(new DragDropItem(48, "Item 48",R.drawable.ic_launcher));
		items.add(new DragDropItem(49, "Item 49",R.drawable.ic_launcher));
		items.add(new DragDropItem(50, "Item 50",R.drawable.ic_launcher));
		items.add(new DragDropItem(51, "Item 51",R.drawable.ic_launcher));
		items.add(new DragDropItem(52, "Item 52",R.drawable.ic_launcher));
		items.add(new DragDropItem(53, "Item 53",R.drawable.ic_launcher));
		items.add(new DragDropItem(54, "Item 54",R.drawable.ic_launcher));
		items.add(new DragDropItem(55, "Item 55",R.drawable.ic_launcher));
		items.add(new DragDropItem(56, "Item 56",R.drawable.ic_launcher));
		items.add(new DragDropItem(57, "Item 57",R.drawable.ic_launcher));
		items.add(new DragDropItem(58, "Item 58",R.drawable.ic_launcher));
		items.add(new DragDropItem(59, "Item 59",R.drawable.ic_launcher));
		DragDropItem lastItem = new DragDropItem(60, "Item 60", R.drawable.ic_launcher);
		lastItem.setIsMoveable(false);
		items.add(lastItem);
	}

	@Override
	public View getView(int index) {
		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		ImageView icon = new ImageView(context);
		DragDropItem item = items.get(index);
		icon.setImageResource(item.getDrawable());
		icon.setPadding(15, 15, 15, 15);
		
		layout.addView(icon);
		
		TextView label = new TextView(context);
		label.setText(item.getName());	
		label.setTextColor(Color.BLACK);
		label.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
	
		label.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		layout.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				return gridview.onLongClick(v);
			}
		});

		layout.addView(label);
		return layout;
	}

	@Override
	public int getRowCount() {
		return AUTOMATIC;
	}

	@Override
	public int getColumnCount() {
		return AUTOMATIC;
	}
	
	@Override
	public DragDropItem getItem(int index) {
		return items.get(index);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	@Override
	public void swapItems(int index1, int index2) {
		Collections.swap(items, index1, index2);
	}

	@Override
	public void deleteItem(int itemIndex) {
		deleteItem(itemIndex);
	}

	@Override
	public void printLayout() {
		for (DragDropItem item : items) {
			Log.d("Item ", Long.toString(item.getId()));
		}
	}
	
}
