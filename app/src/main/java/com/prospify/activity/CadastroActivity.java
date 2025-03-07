package com.prospify.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.prospify.R;
import com.prospify.config.ConfiguracaoFirebase;
import com.prospify.helper.Base64Custom;
import com.prospify.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);



        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);

        botaoCadastrar.setOnClickListener(view -> {
            String textoNome = campoNome.getText().toString();
            String textoEmail = campoEmail.getText().toString();
            String textoSenha = campoSenha.getText().toString();

            //Validar se os campos foram preenchidos
            if (!textoNome.isEmpty()){
                if (!textoEmail.isEmpty()){
                    if (!textoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setNome(textoNome);
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        cadastrarUsuario();

                    }else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o email!",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o nome!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                usuario.setIdUsuario(idUsuario);
                usuario.salvar();
                finish();
            }else {
                String excecao = "";
                try {
                    throw task.getException();
                }catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte.";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Digite um e-mail válido.";
                }catch (FirebaseAuthUserCollisionException e){
                    excecao = "Essa conta ja está cadastrada.";
                }catch(Exception e){
                    excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(CadastroActivity.this,
                        excecao,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}