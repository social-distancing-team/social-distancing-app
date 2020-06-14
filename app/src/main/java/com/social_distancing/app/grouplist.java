package com.social_distancing.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/*
OS
 */
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/*
Tasks, Async and Firebase
 */
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;

/*
Helper class and functions written by us
 */
import com.social_distancing.app.HelperClass;
import com.social_distancing.app.HelperClass.Collections;
import com.social_distancing.app.HelperClass.LOG;
import com.social_distancing.app.HelperClass.User;

/*
Collections
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class grouplist extends AppCompatActivity {
	
	Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grouplist);
		getSupportActionBar().hide();
		
		final String listID = getIntent().getStringExtra("listID");
		final String listName = getIntent().getStringExtra("listName");
		
		final TextView textView_listName = (TextView)findViewById(R.id.textView_listName);
		textView_listName.setText(listName);
		
		final ListView listView_Lists = (ListView)findViewById(R.id.listView_Lists);
		
		Button button_AddItem = (Button)findViewById(R.id.button_AddItem);
		final EditText editText_NewItem = (EditText)findViewById(R.id.editText_NewItem);
		
		final DocumentReference listDocumentReference = (DocumentReference)HelperClass.db.collection(Collections.LISTS).document(listID);
		listDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
			@Override
			public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
				Map<String, Object> listData = documentSnapshot.getData();
				textView_listName.setText(listData.get(Collections.Lists.NAME).toString());
				
				if (listData.get(Collections.Lists.ITEMS).equals("") || ((ArrayList<String>)listData.get(Collections.Lists.ITEMS)).size() == 0)
					return;
				
				ArrayList<String> items = (ArrayList<String>) listData.get("Items");
				final ArrayAdapter<String> adapterListItems = new ArrayAdapter<String>(context,
						android.R.layout.simple_list_item_1, android.R.id.text1, items);
				
				listView_Lists.setAdapter(adapterListItems);
				
				//When the item is clicked, open the deletion dialog
				listView_Lists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
						final AlertDialog.Builder deleteItemDialog = new AlertDialog.Builder(context);
						deleteItemDialog.setTitle("Delete Item");// + listView_Friends.getItemAtPosition(position).toString() + "?");
						deleteItemDialog.setMessage("Are you sure you want to delete this item?");
						
						deleteItemDialog.setNegativeButton("No", null);
						deleteItemDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Map<String, Object> newListItemMap = new HashMap<>();
								String itemValue = listView_Lists.getItemAtPosition(position).toString();
								newListItemMap.put("Items", FieldValue.arrayRemove(itemValue));
								listDocumentReference.update(newListItemMap);
							}
						});
						
						
						final AlertDialog dialog = deleteItemDialog.create();
						dialog.show();
					}
				});
			}
		});
		
		//When the add item button is clicked
		button_AddItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String, Object> newListItemMap = new HashMap<>();
				String itemValue = editText_NewItem.getText().toString();
				newListItemMap.put(Collections.Lists.ITEMS, FieldValue.arrayUnion(itemValue));
				listDocumentReference.update(newListItemMap);
				
				editText_NewItem.onEditorAction(EditorInfo.IME_ACTION_DONE);
				editText_NewItem.setText("");
			}
		});
	}
}
