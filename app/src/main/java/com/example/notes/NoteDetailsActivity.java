package com.example.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.api.Distribution;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class NoteDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE=2;
    ImageView imageNote;
    LinearLayout imageNoteLayout;
    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title, content, docId;
    SeekBar fontSizeSeekBar;
    int fontSize;
    boolean bold=false, italic=false, underlined=false;
    ImageButton boldBtn, italicBtn, underlinedBtn, colorBtn;
    ImageButton menuBtn;
    boolean isEditMode=false;
    int colorIdx=0;
    String selectedImagePath="";
    Uri camImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText=findViewById(R.id.notesTitleText);
        contentEditText=findViewById(R.id.notesContentText);
        saveNoteBtn=findViewById(R.id.saveNoteBtn);
        pageTitleTextView=findViewById(R.id.pageTitle);
        fontSizeSeekBar=findViewById(R.id.fontSizeSeekBar);
        menuBtn=findViewById(R.id.menuBtn);
        boldBtn=findViewById(R.id.boldBtn);
        italicBtn=findViewById(R.id.italicBtn);
        underlinedBtn=findViewById(R.id.underlineBtn);
        colorBtn=findViewById(R.id.colorBtn);
        imageNote=findViewById(R.id.imageNote);
        imageNoteLayout=findViewById(R.id.imageNoteLayout);

        title=getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");
        docId=getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty())
        {
            isEditMode=true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode)
        {
            pageTitleTextView.setText("Edit Note");
            menuBtn.setOnClickListener(v->showMenu());
            fontSize=getIntent().getIntExtra("fontSize", 8);
            fontSizeSeekBar.setProgress(fontSize);
            contentEditText.setTextSize(fontSize+8);

            bold=getIntent().getBooleanExtra("bold", false);
            italic=getIntent().getBooleanExtra("italic", false);
            setTypeFace(bold, italic);

            underlined=getIntent().getBooleanExtra("underlined", false);
            setUnderlined(underlined);

            colorIdx=Utility.colors.indexOf(getIntent().getStringExtra("color"));
            setColor(colorIdx);

            selectedImagePath=getIntent().getStringExtra("imagePath");
            if(!selectedImagePath.equals("")) {
                setImage(Uri.parse("file://" + selectedImagePath));
            }
        }

        initMenu();

        saveNoteBtn.setOnClickListener(v->{saveNoteBtn.setOnClickListener(null); saveNote();});

        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                contentEditText.setTextSize(Integer.valueOf(i)+8);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        boldBtn.setOnClickListener(v->
        {
            bold=!bold;
            setTypeFace(bold, italic);
        });

        italicBtn.setOnClickListener(v->
        {
            italic=!italic;
            setTypeFace(bold, italic);
        });

        underlinedBtn.setOnClickListener(v->
        {
            underlined=!underlined;
            setUnderlined(underlined);
        });

        colorBtn.setOnClickListener(v->
        {
            colorIdx++;
            if(colorIdx>=Utility.colors.size())
            {
                colorIdx=0;
            }
            setColor(colorIdx);
        });

        imageNoteLayout.setOnClickListener(v->
        {
            imageNoteLayout.setVisibility(View.GONE);
            selectedImagePath="";
        });
    }

    void setTypeFace(boolean isBold, boolean isItalic)
    {
        if(isBold && isItalic)
        {
            contentEditText.setTypeface(contentEditText.getTypeface(), Typeface.BOLD_ITALIC);
        }
        else if(isBold)
        {
            contentEditText.setTypeface(contentEditText.getTypeface(), Typeface.BOLD);
        }
        else if(isItalic)
        {
            contentEditText.setTypeface(contentEditText.getTypeface(), Typeface.ITALIC);
        }
        else
        {
            contentEditText.setTypeface(null, Typeface.NORMAL);
        }
    }

    void setUnderlined(boolean isUnderlined)
    {
        if (isUnderlined)
        {
            contentEditText.setPaintFlags(contentEditText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        else
        {
            contentEditText.setPaintFlags(contentEditText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
    }

    void setColor(int colorIdx)
    {
        contentEditText.setTextColor(Color.parseColor(Utility.colors.get(colorIdx)));
        contentEditText.setHintTextColor(Color.parseColor(Utility.colors.get(colorIdx)));
        int btnColorIndex=colorIdx+1;
        if(btnColorIndex>=Utility.colors.size())
        {
            btnColorIndex=0;
        }
        colorBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(Utility.colors.get(btnColorIndex))));
    }

    void deleteNoteFromFirebase()
    {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(NoteDetailsActivity.this, "Note deleted successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    Toast.makeText(NoteDetailsActivity.this, "Failed deleting note", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void saveNote()
    {
        String noteTitle=titleEditText.getText().toString();
        String noteContent=contentEditText.getText().toString();
        fontSize=fontSizeSeekBar.getProgress();
        if(noteTitle==null || noteTitle.isEmpty())
        {
            titleEditText.setError("Title is required");
            saveNoteBtn.setOnClickListener(v->{saveNoteBtn.setOnClickListener(null); saveNote();});
            return;
        }
        Note note=new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        note.setFontSize(fontSize);
        note.setBold(bold);
        note.setItalic(italic);
        note.setUnderlined(underlined);
        note.setColor(Utility.colors.get(colorIdx));
        note.setImagePath(selectedImagePath);
        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note)
    {
        DocumentReference documentReference;
        if(isEditMode)
        {
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }
        else
        {
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(NoteDetailsActivity.this, "Note added successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    Toast.makeText(NoteDetailsActivity.this, "Failed adding note", Toast.LENGTH_LONG).show();
                    saveNoteBtn.setOnClickListener(v->{saveNoteBtn.setOnClickListener(null); saveNote();});
                }
            }
        });
    }

    void showMenu()
    {
        PopupMenu popupMenu=new PopupMenu(NoteDetailsActivity.this, menuBtn);
        popupMenu.getMenu().add("Delete note");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if(menuItem.getTitle()=="Delete note")
                {
                    deleteNoteFromFirebase();
                }
                return false;
            }
        });
    }

    private void initMenu()
    {
        final LinearLayout layoutMenu=findViewById(R.id.menuLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior=BottomSheetBehavior.from(layoutMenu);
        bottomSheetBehavior.setPeekHeight(156);
        layoutMenu.findViewById(R.id.menuText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(bottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_EXPANDED)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        layoutMenu.findViewById(R.id.addImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), android.Manifest.permission.READ_MEDIA_IMAGES
                )!=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(
                            NoteDetailsActivity.this,
                            new String[] {android.Manifest.permission.READ_MEDIA_IMAGES},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }
                else
                {
                    selectImage();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION&&grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                selectImage();
            }
            else
            {
                Toast.makeText(NoteDetailsActivity.this, "Permissions denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = null;
            if(data!=null) {
                // When the image is picked from the gallery, it returns an Uri
                selectedImageUri = data.getData();
            }
            else
            {
                File file = new File(String.valueOf(camImgUri));
                if (!file.getAbsolutePath().equals("/")) {
                    selectedImageUri = camImgUri;
                }
            }

            if (selectedImageUri != null) {
                setImage(selectedImageUri);
            }
        }
    }

    private void selectImage()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        String camImgFilename = sdf.format(new Date())+".jpg";

        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), camImgFilename);

        camImgUri=Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camImgUri);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);

        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        if(chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    void setImage(Uri selectedImageUri)
    {
        try
        {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
            imageNote.setImageBitmap(bitmap);
            imageNoteLayout.setVisibility(View.VISIBLE);

            selectedImagePath=getPathFromUri(selectedImageUri);
        }
        catch (Exception e)
        {
            imageNote.setBackgroundResource(R.drawable.baseline_broken_image_24);
            imageNoteLayout.setVisibility(View.VISIBLE);
            selectedImagePath="";
            Toast.makeText(NoteDetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getPathFromUri(Uri contentUri)
    {
        String filePath;
        Cursor cursor=getContentResolver().query(contentUri, null, null, null, null);
        if(cursor==null)
        {
            filePath=contentUri.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int index=cursor.getColumnIndex("_data");
            filePath=cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
}