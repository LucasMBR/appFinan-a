package com.prospify.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.prospify.R;
import com.prospify.config.ConfiguracaoFirebase;
import com.prospify.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoEntrar = findViewById(R.id.buttonEntrar);

        botaoEntrar.setOnClickListener(v -> {

        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if (!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){

                usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);
                validarLogin();

            }else {
                Toast.makeText(LoginActivity.this,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT).show();
        }
    });
    }

    public void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else{
                String excecao = "";
                try {
                    throw task.getException();
                }catch (FirebaseAuthInvalidUserException e){
                    excecao = "Usuário não está cadastrado.";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Verifique e-mail e senha digitados e tente novamente.";
                }catch(Exception e){
                    excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this,
                        excecao,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}