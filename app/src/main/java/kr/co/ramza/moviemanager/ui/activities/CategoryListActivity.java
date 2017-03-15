package kr.co.ramza.moviemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.RealmResults;
import kr.co.ramza.moviemanager.R;
import kr.co.ramza.moviemanager.di.component.ActivityComponent;
import kr.co.ramza.moviemanager.di.component.DaggerActivityComponent;
import kr.co.ramza.moviemanager.model.Category;
import kr.co.ramza.moviemanager.presenter.CategoryListPresenter;
import kr.co.ramza.moviemanager.ui.adapter.CategoryListAdapter;
import kr.co.ramza.moviemanager.ui.helper.SimpleItemTouchHelperCallback;
import kr.co.ramza.moviemanager.ui.view.CategoryListView;

public class CategoryListActivity extends BaseActivity implements CategoryListView{

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.categoryNameEditText)
    EditText categoryNameEditText;

    @BindView(R.id.addCategoryBtn)
    Button addCategoryBtn;

    @Inject
    CategoryListPresenter categoryListPresenter;

    @Inject
    CategoryListAdapter categoryListAdapter;

    private ItemTouchHelper itemTouchHelper;

    @Override
    protected int getContentViewResource() {
        return R.layout.activity_category_list;
    }

    @Override
    protected ActivityComponent getInitializeComponent() {
        return DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .build();
    }

    @Override
    protected void onInject(@Nullable ActivityComponent component) {
        if (component != null) {
            component.inject(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryListPresenter.setView(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(categoryListAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(categoryListAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        RxView.clicks(addCategoryBtn)
                .map((v)->categoryNameEditText.getText().toString())
                .filter(categoryName->!categoryName.equals(""))
                .subscribe(categoryName->{
                    categoryListPresenter.addCategory(categoryName);
                    categoryNameEditText.setText(null);
                });

        categoryListPresenter.loadCategoryList();
    }

    @Override
    public void showList(RealmResults<Category> categoryRealmResults) {
        categoryListAdapter.setCategoryRealmResults(categoryRealmResults);
        categoryListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(@StringRes int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show();
    }

    public static Intent getIntent(Context context){
        return new Intent(context, CategoryListActivity.class);
    }
}
