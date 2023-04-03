package com.example.iotim.ui.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.iotim.R;
import com.example.iotim.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DashboardFragment extends Fragment {


    private String TAG = DashboardFragment.class.getSimpleName();
    private FragmentDashboardBinding binding;
    private ListView listview = null;
    private ImageView imageView = null;
    private ListViewAdapter adapter = null;
    //파이어 베이스의 스토리지의 폴더와 연결하는 객체
    StorageReference listRef = FirebaseStorage.getInstance().getReference().child("image_store");
    int i = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);



        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.listview = binding.listview;   //화면의 listview객체
        adapter = new ListViewAdapter();    //listview의 어댑터객체

        Log.d("app", "리스트 대기");
        //파이어 베이스 스토리지에 저장된 이미지들을 가져온다.
        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    //모든 파일을 가져오는 메소드
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                        }
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            String name = item.getName().substring(0,item.getName().length()-4);//이미지 파일의 .png를 제거해서 날짜(이름)로 사용

                            final long ONE_MEGABYTE = 1024 * 1024;
                            item.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {//이미지 파일을 Byte형태로
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );//Byte를 bitmap으로 변경
                                    adapter.addItem(new ListItem(Integer.toString(i++), name, bitmap));//결과 리스트에 번호,날짜,bitmap정보 추가
                                    Comparator<ListItem> sortList = new Comparator<ListItem>() { //리스트의 정렬방법 설정
                                        @Override
                                        public int compare(ListItem item1, ListItem item2) {
                                            int ret ;
                                            if (item1.getName().compareTo(item2.getName()) > 0)
                                                ret = -1 ;
                                            else if (item1.getName().compareTo(item2.getName()) < 0)
                                                ret = 1 ;
                                            else
                                                ret = 0 ;
                                            return ret ;
                                        }
                                    } ;

                                    Collections.sort(adapter.items, sortList);  //리스트 최근 날짜순으로 정렬
                                    for(int i = 1; i<=adapter.items.size();i++){
                                        adapter.items.get(i-1).setNum(Integer.toString(i));//각 아이템의 번호 변경
                                    }
                                    adapter.notifyDataSetChanged();
                                    listview.setAdapter(adapter);//리스트 출력
                                    Log.d("app", "리스트 추가");
                                    // Data for "images/island.jpg" is returns, use this as needed
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
        Log.d("app", "리스트 출력");
        return root;
    }
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<ListItem> items = new ArrayList<ListItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(ListItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final ListItem listItem = items.get(position);//해당 위치의 리스트 아이템

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_list_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }
            //화면 리스트아이템의 각 객체들
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_date);
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            //리스트아이템의 번호, 날짜, 사진 설정.
            tv_num.setText(listItem.getNum());
            tv_name.setText(listItem.getName());
            iv_icon.setImageBitmap(listItem.getResId());
            Log.d(TAG, "getView() - [ "+position+" ] "+listItem.getName());

            return convertView;  //뷰 객체 반환
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}