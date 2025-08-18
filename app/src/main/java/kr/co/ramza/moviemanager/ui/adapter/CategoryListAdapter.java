package kr.co.ramza.moviemanager.ui.adapter;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import javax.inject.Inject;

import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.databinding.ItemCategoryListBinding;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.model.interactor.RealmInteractor;
import kr.co.ramza.moviemanager.ui.helper.ItemTouchHelperAdapter;
import kr.co.ramza.moviemanager.ui.helper.ItemTouchHelperViewHolder;

/**
 * Created by 전창현 on 2017-02-28.
 * ACTIVE D&C
 * ramza@activednc.com
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private RealmResults<Category> categoryRealmResults;

    private RealmInteractor realmInteractor;

    @Inject
    public CategoryListAdapter(RealmInteractor realmInteractor) {
        this.realmInteractor = realmInteractor;
    }

    public void setCategoryRealmResults(RealmResults<Category> categoryRealmResults) {
        this.categoryRealmResults = categoryRealmResults;
    }

    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
        ViewHolder vh = new ViewHolder(v, new CategoryNameTextWatcher());
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Category category = categoryRealmResults.get(position);
        holder.categoryNameTextWatcher.setCategory(category);
        holder.binding.categoryNameEditText.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryRealmResults != null ? categoryRealmResults.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Category category = categoryRealmResults.get(position);
        realmInteractor.deleteCategory(category);
        notifyItemRemoved(position);
    }

    public class CategoryNameTextWatcher implements TextWatcher {
        private Category category;

        public void setCategory(Category category) {
            this.category = category;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            realmInteractor.modifyCategoryName(category, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        final ItemCategoryListBinding binding;

        CategoryNameTextWatcher categoryNameTextWatcher;

        public ViewHolder(View itemView, CategoryNameTextWatcher categoryNameTextWatcher) {
            super(itemView);
            binding = ItemCategoryListBinding.bind(itemView);
            this.categoryNameTextWatcher = categoryNameTextWatcher;
            this.binding.categoryNameEditText.addTextChangedListener(categoryNameTextWatcher);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
