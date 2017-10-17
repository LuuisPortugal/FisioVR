package br.cesupa.fisiovr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.cesupa.fisiovr.list.FisioterapeutaListActivity;
import br.cesupa.fisiovr.list.PacienteListActivity;
import br.cesupa.fisiovr.list.SessaoListActivity;
import br.cesupa.fisiovr.list.VideoListActivity;
import br.cesupa.fisiovr.util.Util;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private View activity_login_include;
    private View activity_content_home_include;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mEmailView = (TextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        activity_login_include = findViewById(R.id.activity_login_include);
        activity_content_home_include = findViewById(R.id.activity_content_home_include);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                updateView(user);
            }
        };

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        updateView(mAuth.getCurrentUser());
    }

    //Atualiza as Views
    private void updateView(FirebaseUser user) {
        if (user != null) {
            activity_login_include.setVisibility(View.GONE);
            activity_content_home_include.setVisibility(View.VISIBLE);

            String nomeDisplay = user.getDisplayName();
            ((TextView) findViewById(R.id.activity_content_home_include_text_view))
                    .setText(String.format("Bem vindo, %s", TextUtils.isEmpty(nomeDisplay) ? "nome não cadastrado" : nomeDisplay));
        } else {
            activity_login_include.setVisibility(View.VISIBLE);
            activity_content_home_include.setVisibility(View.GONE);
        }
    }

    //Executa a ação de ir para outra pagina
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mAuth.getCurrentUser() != null) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_sessao_iniciar) {
                Toast.makeText(this, "nav_sessao_iniciar", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_sessao_Lista) {
                startActivity(new Intent(this, SessaoListActivity.class));
            } else if (id == R.id.nav_videos_novos) {
                startActivity(new Intent(this, VideoListActivity.class));
            } else if (id == R.id.nav_videos_salvos) {
            } else if (id == R.id.nav_pessoas_fisioterapeutas) {
                startActivity(new Intent(this, FisioterapeutaListActivity.class));
            } else if (id == R.id.nav_pessoas_pacientes) {
                startActivity(new Intent(this, PacienteListActivity.class));
            }
        } else {
            Toast.makeText(this, getString(R.string.error_non_logged), Toast.LENGTH_SHORT).show();
            mEmailView.requestFocus();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Validate Email
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    //Validate Senha
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    //Loga
    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Util.showProgress(this, mLoginFormView, mProgressView, true);
            mAuth.signInWithEmailAndPassword(mEmailView.getText().toString(), mPasswordView.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Util.showProgress(home.this, mLoginFormView, mProgressView, true);
                            if (!task.isSuccessful()) {
                                updateView(task.getResult().getUser());
                                Toast.makeText(home.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mEmailView.requestFocus();
                            }
                        }
                    });
        }

        mPasswordView.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
            if (mAuth != null) {
                mEmailView.setText(mAuth.getCurrentUser().getEmail());

                mAuth.signOut();
                mPasswordView.requestFocus();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
