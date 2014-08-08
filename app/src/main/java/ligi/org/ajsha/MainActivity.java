package ligi.org.ajsha;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import bsh.EvalError;
import bsh.Interpreter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    public static final String CODE_KEY = "code";
    private Interpreter interpreter;

    @InjectView(R.id.exception_out)
    TextView exceptionOut;

    @InjectView(R.id.obj_out)
    TextView objOut;


    @InjectView(R.id.out_stream)
    TextView streamedOutTV;

    @InjectView(R.id.obj_tostring)
    TextView toStringTV;

    @InjectView(R.id.codeInput)
    EditText codeInput;

    private String streamedOutString;

    @OnClick(R.id.execCodeButton)
    void execCodeonClick() {
        try {
            streamedOutString = "";
            streamedOutTV.setText(streamedOutString);

            final Object eval = interpreter.eval(codeInput.getText().toString());

            exceptionOut.setText("");
            if (eval == null) {
                objOut.setText("VOID");
                toStringTV.setText("");
            } else {

                objOut.setText(eval.getClass().toString());

                toStringTV.setText(eval.toString());
            }
        } catch (EvalError evalError) {
            exceptionOut.setText("" + evalError);
            evalError.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        interpreter = new Interpreter();

        OutputStream streamedOutStream = new OutputStream() {
            @Override
            public void write(int oneByte) throws IOException {
                streamedOutString += (char) oneByte;
                streamedOutTV.setText(MainActivity.this.streamedOutString);
            }
        };

        interpreter.setOut(new PrintStream(streamedOutStream));
        interpreter.setErr(new PrintStream(streamedOutStream));

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String showedCode = sharedPrefs.getString(CODE_KEY, getString(R.string.hello_world_code));
        codeInput.setText(showedCode);

        codeInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            PreferenceManager.getDefaultSharedPreferences(this).
                    edit()
                    .putString(CODE_KEY, codeInput.getText().toString())
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
