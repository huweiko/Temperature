package com.refeved.monitor.view;

import java.lang.reflect.Field;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.GridView;
import com.refeved.monitor.adapter.GridAdapter;

public class OptimizeGridView extends GridViewBase {
    /**
     * 要用能包含重复元素的集合
     *
     * @param <T>
     */
    public interface OptimizeGridAdapter<T> {
        List<T> getItems();
        /**
         * Should notify the listView data is changed
         *
         * @param items
         */
        void setItems(List<T> items);
        void setColumn(int column);
        T getNullItem();
        boolean isNullItem(T item);
    }
    
    GridAdapter mAdapter;

    public OptimizeGridView(Context context) {
        super(context);
        
        mAdapter = new GridAdapter(context);
        this.setAdapter(mAdapter);
    }
    
    public OptimizeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mAdapter = new GridAdapter(context);
        this.setAdapter(mAdapter);
    }

    public OptimizeGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mAdapter = new GridAdapter(context);
        this.setAdapter(mAdapter);
    }

    @SuppressLint("DrawAllocation")
    @SuppressWarnings({ "rawtypes" })
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int numColumns = AUTO_FIT;

        try {
            Field numColumnsField = GridView.class.getDeclaredField("mNumColumns");
                numColumnsField.setAccessible(true);
                numColumns = numColumnsField.getInt(this);
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
        }
 
        if (numColumns != AUTO_FIT) {
            final Adapter adapter = getAdapter();
            if (!(adapter instanceof OptimizeGridAdapter)) {
                return;
            }
            
			final OptimizeGridAdapter adapter2 = (OptimizeGridAdapter) adapter;
			adapter2.setColumn(numColumns);
          
//            final int count = adapter.getCount();
//            final int remainder = count % numColumns;
//            if (remainder != 0) {
//                final int diff = numColumns - remainder;
//                final OptimizeGridAdapter adapter2 = (OptimizeGridAdapter) adapter;
//                final List items = new ArrayList();
//                items.addAll(adapter2.getItems());
//                for (int i = 0; i < diff; i++) {
//                    items.add(adapter2.getNullItem());
//                }
//                adapter2.setItems(items);
//            }
        }
    }
    

	@SuppressWarnings("unchecked")
	@Override
    public void updateListView(@SuppressWarnings("rawtypes") List listItems) {
		// TODO Auto-generated method stub
    	mAdapter.setItems(listItems);
    	mAdapter.notifyDataSetChanged();
	}
    
    @SuppressWarnings("rawtypes")
	@Override
	public List getItems() {
		// TODO Auto-generated method stub
		return mAdapter.getItems();
	}

}
