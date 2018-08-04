package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;
    private Random numeroRadomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle canoTopoRetangulo;
    private Rectangle canoBaixoRetangulo;

    private int estadoJogo = 0;
    private int pontuacao = 0;

    private float larguraDispositivo;
    private float alturaDispositivo;
    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posiciaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private float alturaEntreCanosRandomica;

    private boolean marcouPonto;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

    @Override
    public void create() {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonte.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        batch = new SpriteBatch();
        numeroRadomico = new Random();

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        
        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;
        posiciaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;

        espacoEntreCanos = 300;

        parameter.size = 64;
        fonte = generator.generateFont(parameter);
        fonte.setColor(Color.WHITE);

        parameter.size = 48;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        mensagem = generator.generateFont(parameter);
        mensagem.setColor(Color.WHITE);

        passaroCirculo = new Circle();

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        generator.dispose();
    }

    @Override
    public void render() {

        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (variacao > 2) variacao = 0;
        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 8;

        if (estadoJogo == 0) {
            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }
        } else {

            velocidadeQueda++;
            if (posiciaoInicialVertical > 0 || velocidadeQueda < 0) {
                posiciaoInicialVertical -= velocidadeQueda;
            }

            if (estadoJogo == 1) {

                posicaoMovimentoCanoHorizontal -= deltaTime * 200;


                if (Gdx.input.justTouched()) {
                    velocidadeQueda -= 20;
                }

                //Verifica se o cano saiu inteiramente da tela
                if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRadomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                if (posicaoMovimentoCanoHorizontal < 120) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            }
            else {
                if (Gdx.input.justTouched()) {
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posiciaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                }
            }
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
            batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
            batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
            batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);
            batch.draw(passaros[(int) variacao], 120, posiciaoInicialVertical);
            fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50, 0, 1, false);
            if (estadoJogo == 2){
                batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
                mensagem.draw(batch, "Toque na tela para reiniciar!", larguraDispositivo / 2, alturaDispositivo / 2, 0, 1, false);
            }
        batch.end();

        passaroCirculo.set(120 + passaros[0].getWidth() / 2, posiciaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
        canoBaixoRetangulo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoBaixo.getWidth(), canoBaixo.getHeight());
        canoTopoRetangulo = new Rectangle(posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica, canoTopo.getWidth(), canoTopo.getHeight());

        if (Intersector.overlaps(passaroCirculo, canoBaixoRetangulo) || Intersector.overlaps(passaroCirculo, canoTopoRetangulo) || posiciaoInicialVertical <= 0 || posiciaoInicialVertical >= alturaDispositivo) {
            estadoJogo = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
