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

import android.view.View;

public interface DragDropGridAdapter {

	public final static int AUTOMATIC = -1;

	/**
	 * Returns the item count
	 * 
	 * @return item count for page
	 */
	public int getItemCount();

	/**
	 * Returns the item at the specified index
	 * 
	 * @return item count for page
	 */
	public DragDropItem getItem(int index);
	
	/**
	 * Returns the view for an item
	 * 
	 * @param item index
	 * @return the view 
	 */
	public View getView(int index);
	
	/**
	 * The fixed row count (AUTOMATIC for automatic computing)
	 * 
	 * @return row count or AUTOMATIC
	 */
	public int getRowCount();
	
	/**
	 * The fixed column count (AUTOMATIC for automatic computing)
	 * 
	 * @return column count or AUTOMATIC
	 */
	public int getColumnCount();

	/**
	 * Prints the layout in Log.d();
	 */
	public void printLayout();

	/**
	 * Swaps two items in he item list
	 * 
	 * @param itemIndexA
	 * @param itemIndexB
	 */
	public void swapItems(int index1, int index2);

	
	/**
	 * deletes the item
	 * 
	 * @param itemIndex
	 */
	public void deleteItem( int itemIndex);
}
