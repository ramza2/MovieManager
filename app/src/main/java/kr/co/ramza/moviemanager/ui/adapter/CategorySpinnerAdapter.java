package kr.co.ramza.moviemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;

/**
 * Created by 전창현 on 2017-03-02.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class CategorySpinnerAdapter extends BaseAdapter{

    private ArrayList<Category> categories;

    @Inject
    public CategorySpinnerAdapter(RealmInteractor realmInteractor) {
        categories = new ArrayList<>(realmInteractor.getAllCategories());
    }

    public void addTotalData(String all){
        Category category = new Category();
        category.setName(all);
        category.setId(0);
        categories.add(0, category);
    }

    public ArrayList<Category> getCategories(){
        return this.categories;
    }

    @Override
    public int getCount() {
        return categories != null ? categories.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);

        Category category = categories.get(position);
        textView.setText(category.getName());
        return convertView;
    }
}
