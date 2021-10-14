package com.example.myapplication.myfragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myapplication.MainActivity.mUser;

public class SearchFragment extends Fragment {
    private TextView wordField;
    private TextView meanField;
    private TextView exampleField;
    private Button button;
    private LinearLayout wordArea;
    public static String word = "";
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_search, container, false);
        wordField = view.findViewById(R.id.word_field);
        meanField = view.findViewById(R.id.mean_field);
        exampleField = view.findViewById(R.id.example_field);
        button = view.findViewById(R.id.save_button);
        wordArea = view.findViewById(R.id.word_area);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button.OnClickListener saveButtonListener = new View.OnClickListener() {
            private boolean isExisted(String word) {
                Log.d("测试", "加载单词");
                FileInputStream in = null;
                BufferedReader reader = null;
                try {
                    in = getActivity().openFileInput(mUser.getName());
                    reader = new BufferedReader(new InputStreamReader(in));
                    LinearLayout wordArea = getActivity().findViewById(R.id.word_area);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals(word)) return true;
                    }
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            private boolean saveWord(String word) {
                FileOutputStream out = null;
                BufferedWriter writer = null;
                try {
                    out = getActivity().openFileOutput(mUser.getName(), Context.MODE_APPEND);   // MODE_APPEND写入到末尾
                    writer = new BufferedWriter(new OutputStreamWriter(out));
                    writer.write(word + "\n");
                    Log.d("写文件", "完成");
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                finally {
                    try {
                        if (writer != null) writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void onClick(View view) {
                if(mUser != null) {
                    if (isExisted(word))
                        Toast.makeText(getActivity(), "单词已存在", Toast.LENGTH_SHORT).show();

                    else {
                        if (saveWord(word))
                            Toast.makeText(getActivity(), "成功保存到单词本", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                    }

                    // 保存完后按钮重新设为不可用
                    view.setClickable(false);
                    //测试用
                    //Intent intent = new Intent(SearchActivity.this, WordBookActivity.class);
                    //intent.putExtra("wordBookFile", wordBookFile);
                    //intent.putExtra("logged", true);
                    //startActivity(intent);
                }
                else
                    Toast.makeText(getActivity(), "请登录后使用该功能", Toast.LENGTH_SHORT).show();
            }
        };

        SearchView.OnQueryTextListener searchBoxListener = new SearchView.OnQueryTextListener() {
            /** 基于词霸的英语单词解析和翻译 **/
            private final String ICIBA_API = "http://dict-co.iciba.com/api/dictionary.php";
            private final String KEY = "BA3380AD4E78985180E4294140A4D76D";

            private String key = "";            // 关键词（查询的单词）
            private String pos = "";            // 词性
            private String acceptation = "";    // 对应词性的译文
            private String orig = "";           // 英文例句
            private String trans = "";          // 例句译文

            private void XmlParser(String s) throws Exception {
                key = pos = acceptation = orig = trans = "";

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(s));

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG: {
                            switch (nodeName) {
                                case "key":
                                    key += parser.nextText() + "||";
                                    break;
                                case "pos":
                                    pos += parser.nextText() + "||";
                                    break;
                                case "acceptation":
                                    acceptation += parser.nextText() + "||";
                                    break;
                                case "orig":
                                    orig += parser.nextText() + "||";
                                    break;
                                case "trans":
                                    trans += parser.nextText() + "||";
                                    break;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            if (nodeName.equals("dict")) {  // 字符串相等不能直接==
                                Log.d("key", key);
                                Log.d("pos", pos);
                                Log.d("acceptation", acceptation);
                                Log.d("orig", orig);
                                Log.d("trans", trans);
                            }
                            break;
                        }
                    }
                    eventType = parser.next();
                }
            }

            private void ShowInformation() {
                TextView wordField = getActivity().findViewById(R.id.word_field);
                TextView meanField = getActivity().findViewById(R.id.mean_field);
                TextView exampleField = getActivity().findViewById(R.id.example_field);

                String[] content1, content2;
                String text = "";
                content1 = key.split("\\|\\|");
                text = content1[0];
                //Log.d("查询", text);
                wordField.setText(text);
                word = text;    // 记录与saveButton通讯的单词信息

                text = "";
                content1 = pos.split("\\|\\|");
                content2 = acceptation.split("\\|\\|");
                for (int i = 0; i < content1.length; i++)
                    text += content1[i] + "  " + content2[i];
                //Log.d("释义", text);
                meanField.setText(text);

                text = "";
                content1 = orig.split("\\|\\|");
                content2 = trans.split("\\|\\|");
                for (int i = 0; i < content1.length; i++)
                    text += content1[i] + content2[i] + "\n";
                //Log.d("例句", text);
                exampleField.setText(text);
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                final String url = ICIBA_API + "?w=" + s + "&key=" + KEY;
                Log.d("URL", url);

                try {
                    Thread thread = new Thread(new Runnable() {     // 普通线程中不能用Toast等UI，不然会闪退
                        @Override
                        public void run() {
                            String responseData = "";
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder().url(url).build();
                                Response response = client.newCall(request).execute();  // 必须在子线程中执行
                                responseData = response.body().string();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(getActivity(), "资源获取错误，请检查网络！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                Log.d("错误", "资源获取错误！");
                            }
                            try {
                                XmlParser(responseData);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(getActivity(), "资源解析错误，请检查网络！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                Log.d("错误", "资源解析错误！");
                            }
                        }
                    });
                    thread.start();
                    thread.join();
                    ShowInformation();

                    // 已登陆并查询到单词才可以保存
                    if (MainActivity.mUser != null) {
                        Button button = getActivity().findViewById(R.id.save_button);
                        button.setClickable(true);
                        Log.d("保存按钮", "开启");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.matches("^[a-zA-Z]*") == false)
                    Toast.makeText(getActivity(), "单词包含非法字符！", Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        Button saveButon = getActivity().findViewById(R.id.save_button);
        saveButon.setOnClickListener(saveButtonListener);
        SearchView searchBox = getActivity().findViewById(R.id.search_box);
        searchBox.setOnQueryTextListener(searchBoxListener);
    }
}
