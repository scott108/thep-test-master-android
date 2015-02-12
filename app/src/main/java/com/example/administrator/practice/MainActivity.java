package com.example.administrator.practice;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigInteger;

import thep.paillier.EncryptedInteger;
import thep.paillier.PrivateKey;
import thep.paillier.PublicKey;
import thep.paillier.exceptions.BigIntegerClassNotValid;
import thep.paillier.exceptions.PublicKeysNotEqualException;
import util.TransformMgt;




public class MainActivity extends ActionBarActivity {
    private static final BigInteger threshold = new BigInteger("1000000");
    PrivateKey privkey;
    PublicKey pubkey;
    String myPoints;
    String addPoints;
    String privateKeyString;
    String publicKeyString;
    TextView totalPoints;
    TextView points;
    EditText addPoint;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        privkey = new PrivateKey(128);

        privateKeyString = null;
        publicKeyString = null;

        points = (TextView) findViewById(R.id.points);
        addPoint = (EditText) findViewById(R.id.editTextAddPoint);
        button = (Button) findViewById(R.id.button);
        totalPoints =(TextView) findViewById(R.id.totalPoints);

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPoints = points.getText().toString();
                addPoints = addPoint.getText().toString();

                pubkey = privkey.getPublicKey();

                try {
                    privateKeyString = TransformMgt.toString(privkey);
                    publicKeyString = TransformMgt.toString(pubkey);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    EncryptedInteger myEncryptedPoints = new EncryptedInteger(new BigInteger(myPoints), pubkey);
                    EncryptedInteger addEncryptedPoints = new EncryptedInteger(new BigInteger(addPoints), pubkey);

                    EncryptedInteger total = myEncryptedPoints.add(addEncryptedPoints);

                    if(!privateKeyString.equals(null)) {
                        PrivateKey pri = (PrivateKey) TransformMgt.fromString(privateKeyString);

                        BigInteger decryptedC = total.decrypt(pri);

                        if (decryptedC.compareTo(threshold) == 1)
                            decryptedC = decryptedC.subtract(pubkey.getN());

                        totalPoints.setText(decryptedC+"");
                    }
                } catch (BigIntegerClassNotValid bigIntegerClassNotValid) {
                    bigIntegerClassNotValid.printStackTrace();
                } catch (PublicKeysNotEqualException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
