import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

class Servidor {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(80);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + 80 + ", " + e);
            System.exit(1);
        }

        for (int i = 0; i < 3; i++) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Accept failed: " + 80 + ", " + e);
                System.exit(1);
            }

            System.out.println("Accept Funcionou!");

            new Servindo(clientSocket).start();

        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Servindo extends Thread {
    String nomePersonagem[] = { "Player1Parado", "Player1Mov1", "Player1Mov2", "Player1Mov3", "Player1Mov4",
            "Player1Mov5", "Player1Morte1", "Player1Morte2", "Player1Morte3", "Player1Morte4", "Player1Morte5",
            "Player1Morte6", "Player2Parado", "Player2Mov1", "Player2Mov2", "Player2Mov3", "Player2Mov4", "Player2Mov5",
            "Player2Morte1", "Player2Morte2", "Player2Morte3", "Player2Morte4", "Player2Morte5", "Player2Morte6" };
    final int anda = 3, cliente1 = 0, cliente2 = 1, numCliente = 0, posClienteX = 1, posClienteY = 2, btCliente = 3,
            gravCliente = 4, dirCliente = 5, estadoCliente = 6, posBulletX = 7, posBulletY = 8, bulletGo = 9;
    Socket clientSocket;
    static PrintStream os[] = new PrintStream[3];
    static int cont = 0;
    String vet[] = new String[20];
    int direcao, estadoClient1 = 1, estadoClient2 = 1, estado1, estado2, contPulo = 0, posTiroX = 1030, posTiroY = 1000,
            direcaoTiro, canShoot, isGravityOn = 0;
    boolean isKeySpacePressed = false;
    static int posX1, posY1, posX2, posY2;

    Servindo(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            os[cont] = new PrintStream(clientSocket.getOutputStream());
            os[cont].println(cont);
            os[cont].flush();
        } catch (IOException erro) {
        }
    }

    public int verificaGrav(int posX, int posY, int direcao) {
        MatrizMapa posMap = new MatrizMapa();
        int matX1, matX2, matY1, matY2;
        matX1 = posX / 32;
        matY1 = (posY + 32) / 32;
        if (direcao == 1) {
            matX2 = (posX + 32) / 32;
            matY2 = (posY + 32) / 32;
        } else {
            matX2 = (posX - 32) / 32;
            matY2 = (posY + 32) / 32;
        }
        if (matX1 >= 32 || matY1 >= 22 || matX2 >= 32 || matY2 >= 22) {
            if (matX1 >= 32)
                matX1 = 31;
            if (matY1 >= 32)
                matY1 = 21;
            if (matX2 >= 32)
                matX2 = 31;
            if (matY2 >= 32)
                matY2 = 21;
        }
        if (posMap.matrizMapa[matY1][matX1] == 0 || posMap.matrizMapa[matY2][matX2] == 0)
            return 0;
        return 1;
    }

    public int verificaTeto(int posX, int posY, int direcao) {
        MatrizMapa posMap = new MatrizMapa();
        int matX1, matX2, matY1, matY2;
        matX1 = posX / 32;
        matY1 = posY / 32;
        if (direcao == 1) {
            matX2 = (posX + 32) / 32;
            matY2 = posY / 32;
        } else {
            matX2 = (posX - 32) / 32;
            matY2 = posY / 32;
        }
        if (matX1 >= 32 || matY1 >= 22 || matX2 >= 32 || matY2 >= 22) {
            if (matX1 >= 32)
                matX1 = 31;
            if (matY1 >= 32)
                matY1 = 21;
            if (matX2 >= 32)
                matX2 = 31;
            if (matY2 >= 32)
                matY2 = 21;
        }
        if (posMap.matrizMapa[matY1][matX1] == 0 || posMap.matrizMapa[matY2][matX2] == 0)
            return 0;
        return 1;
    }

    public int verificaWallEsq(int posX, int posY, int width, int height) {
        MatrizMapa posMap = new MatrizMapa();
        int matX1, matX2, matY1, matY2;
        if (posX == 1030 || posY == 1000)
            return 0;
        matX1 = posX / 32;
        matY1 = (posY + height) / 32;
        matX2 = posX / 32;
        matY2 = posY / 32;
        if (matX1 >= 32 || matY1 >= 22 || matX2 >= 32 || matY2 >= 22) {
            if (matX1 >= 32)
                matX1 = 31;
            if (matY1 >= 32)
                matY1 = 21;
            if (matX2 >= 32)
                matX2 = 31;
            if (matY2 >= 32)
                matY2 = 21;
        }
        if (posMap.matrizMapa[matY1][matX1] == 0 || posMap.matrizMapa[matY2][matX2] == 0)
            return 0;
        return 1;
    }

    public int verificaWallDir(int posX, int posY, int width, int height) {
        MatrizMapa posMap = new MatrizMapa();
        int matX1, matX2, matY1, matY2;
        if (posX == 1030 || posY == 1000)
            return 0;
        matX1 = (posX + width) / 32;
        matY1 = posY / 32;
        matX2 = (posX + width) / 32;
        matY2 = (posY + height) / 32;
        if (matX1 >= 32 || matY1 >= 22 || matX2 >= 32 || matY2 >= 22) {
            if (matX1 >= 32)
                matX1 = 31;
            if (matY1 >= 32)
                matY1 = 21;
            if (matX2 >= 32)
                matX2 = 31;
            if (matY2 >= 32)
                matY2 = 21;
        }
        if (posMap.matrizMapa[matY1][matX1] == 0 || posMap.matrizMapa[matY2][matX2] == 0)
            return 0;
        return 1;
    }

    public int verificaHitBoxDir(int coordPlayerX, int coordPlayerY, int coordTiroX, int coordTiroY) {
        /*
         * int verX, verY1, verY2, verTX, verTY1, verTY2; verX = coordPlayerX; verY1 =
         * coordPlayerY; verY2 = coordPlayerY + 32; verTX = coordTiroX + 15; verTY1 =
         * coordTiroY; verTY2 = coordTiroY + 5;
         */
        System.out.println("TX " + coordTiroX);
        System.out.println("TY " + coordTiroY);
        System.out.println("PX " + coordPlayerX);
        System.out.println("PY " + coordPlayerY);
        if (coordPlayerX <= coordTiroX + 15) {
            System.out.println("ENTROU 3 ---------------------------------");
            if (coordTiroY <= coordPlayerY && coordTiroY + 5 >= coordPlayerY + 32) {
                System.out.println("ENTROU ---------------------------------");
                return 1;
            }
        }
        return 0;
    }

    public int verificaHitBoxEsq(int coordPlayerX, int coordPlayerY, int coordTiroX, int coordTiroY) {
        /*
         * int verX, verY1, verY2, verTX, verTY1, verTY2; verX = coordPlayerX+32; verY1
         * = coordPlayerY; verY2 = coordPlayerY + 32; verTX = coordTiroX; verTY1 =
         * coordTiroY; verTY2 = coordTiroY + 5;
         */
        System.out.println("TX " + coordTiroX);
        System.out.println("TY " + coordTiroY);
        System.out.println("PX " + coordPlayerX);
        System.out.println("PY " + coordPlayerY);
        if (coordPlayerX + 32 >= coordTiroX) {
            System.out.println("ENTROU 3 ---------------------------------");
            if (coordTiroY <= coordPlayerY && coordTiroY + 5 >= coordPlayerY + 32) {
                System.out.println("ENTROU ---------------------------------");
                return 1;
            }
        }
        return 0;
    }

    public void verificaTiro(int novaPosTiroX, int novaPosTiroY, int posPersonagemX, int posPersonagemY) {
        if (direcaoTiro == 1 && verificaHitBoxDir(posPersonagemX, posPersonagemY, novaPosTiroX, novaPosTiroY) == 1) {
            System.out.println("ACERTOU PLAYER -----------------------------------");
            posTiroX = 1030;
            posTiroY = 1000;
            canShoot = 0;
        }
        if (direcaoTiro == -1 && verificaHitBoxEsq(posPersonagemX, posPersonagemY, novaPosTiroX, novaPosTiroY) == 1) {
            System.out.println("ACERTOU PLAYER -----------------------------------");
            posTiroX = 1030;
            posTiroY = 1000;
            canShoot = 0;
        }
        if (verificaWallDir(novaPosTiroX, novaPosTiroY, 15, 5) == 0
                || verificaWallEsq(novaPosTiroX, novaPosTiroY, 15, 5) == 0) {
            posTiroX = 1030;
            posTiroY = 1000;
            canShoot = 0;
        }
        if (direcaoTiro == 1 && verificaWallDir(novaPosTiroX, novaPosTiroY, 15, 5) == 1) {
            novaPosTiroX += anda;
            posTiroX = novaPosTiroX;
        }
        if (direcaoTiro == -1 && verificaWallEsq(novaPosTiroX, novaPosTiroY, 15, 5) == 1) {
            novaPosTiroX -= anda;
            posTiroX = novaPosTiroX;
        }
    }

    public void enviaDados(int i, int novaPosX, int novaPosY, int est) {
        os[i].println(vet[numCliente] + " " + novaPosX + " " + novaPosY + " " + vet[btCliente] + " " + isGravityOn + " "
                + direcao + " " + est + " " + posTiroX + " " + posTiroY + " " + canShoot);
        os[i].flush();
    }

    public void run() {
        try {
            Scanner is = new Scanner(clientSocket.getInputStream());
            os[cont++] = new PrintStream(clientSocket.getOutputStream());

            String inputLine;
            do { // distribuição para os clientes
                inputLine = is.nextLine();
                System.out.println(inputLine);
                vet = inputLine.split(" ");
                int novaPosX = Integer.parseInt(vet[posClienteX]);
                int novaPosY = Integer.parseInt(vet[posClienteY]);
                int cliente = Integer.parseInt(vet[numCliente]);
                if (cliente == 0) { // salva a última posição para verificar se o tiro acertou
                    posX1 = novaPosX;
                    posY1 = novaPosY;
                }
                if (cliente == 1) {
                    posX2 = novaPosX;
                    posY2 = novaPosY;
                }
                if (vet[btCliente].compareTo("BULLET") == 0) {
                    int novaPosTiroX = Integer.parseInt(vet[posBulletX]);
                    int novaPosTiroY = Integer.parseInt(vet[posBulletY]);
                    int dirRecebido = Integer.parseInt(vet[dirCliente]);
                    if (vet[bulletGo].compareTo("1") == 0) { // Define posição inicial do tiro
                        if (vet[dirCliente].compareTo("1") == 0) {
                            posTiroX = novaPosX + 46;
                            posTiroY = novaPosY + 16;
                        } else {
                            posTiroX = novaPosX - 76;
                            posTiroY = novaPosY + 16;
                        }
                        direcaoTiro = dirRecebido;
                        canShoot = 1;
                    } else if (vet[bulletGo].compareTo("-1") == 0) {
                        canShoot = 1;
                        if (cliente == 0) {
                            verificaTiro(novaPosTiroX, novaPosTiroY, posX2, posY2);
                        }
                        if (cliente == 1) {
                            verificaTiro(novaPosTiroX, novaPosTiroY, posX1, posY1);
                        }
                    }
                }
                if (vet[btCliente].compareTo("RIGHT") == 0 || vet[btCliente].compareTo("SPACE-AND-RIGHT") == 0) {
                    if (vet[dirCliente].compareTo("-1") == 0)
                        novaPosX -= 32;
                    if (verificaWallDir(novaPosX, novaPosY, 32, 32) == 1)
                        novaPosX += anda;
                    direcao = 1;
                }
                if (vet[btCliente].compareTo("LEFT") == 0 || vet[btCliente].compareTo("SPACE-AND-LEFT") == 0) {
                    if (vet[dirCliente].compareTo("1") == 0)
                        novaPosX += 32;
                    if (verificaWallEsq(novaPosX - 32, novaPosY, 32, 32) == 1)
                        novaPosX -= anda;
                    direcao = -1;
                }

                if ((vet[btCliente].compareTo("SPACE") == 0 || vet[btCliente].compareTo("SPACE-AND-RIGHT") == 0
                        || vet[btCliente].compareTo("SPACE-AND-LEFT") == 0)
                        && (verificaGrav(novaPosX + anda, novaPosY + anda, direcao) == 0)) {
                    isKeySpacePressed = true;
                    contPulo = 0;
                }

                if (vet[numCliente].compareTo("0") == 0 && vet[btCliente].compareTo("A") != 0) {
                    estado1 = estadoClient1;
                    estadoClient1++;
                    if (estadoClient1 == 5)
                        estadoClient1 = 1;
                }
                if (vet[numCliente].compareTo("1") == 0 && vet[btCliente].compareTo("A") != 0) {
                    estado2 = estadoClient2;
                    estadoClient2++;
                    if (estadoClient2 == 5)
                        estadoClient2 = 1;
                }
                if (verificaGrav(novaPosX, novaPosY, direcao) == 1 && !isKeySpacePressed)
                    novaPosY += anda;
                else if (verificaGrav(novaPosX, novaPosY, direcao) == 1 && isKeySpacePressed) {
                    contPulo++;
                    if (verificaTeto(novaPosX, novaPosY, direcao) == 1)
                        novaPosY -= anda;
                    if (contPulo == 64) {
                        isKeySpacePressed = false;
                        contPulo = 0;
                    }
                }
                isGravityOn = verificaGrav(novaPosX, novaPosY, direcao);

                /*
                 * System.out.println("Cliente " + vet[numCliente] + " posX " + novaPosX +
                 * " posY " + vet[posClienteY] + " bt " + vet[btCliente] + " grav " +
                 * verificaGrav(novaPosX, novaPosY, direcao) + " dir " + direcao + " est1 " +
                 * estadoClient1 + " est2 " + estadoClient2);
                 */
                for (int i = 0; i < cont; i++) {
                    if (cliente == 0)
                        enviaDados(i, novaPosX, novaPosY, estado1);
                    else if (cliente == 1)
                        enviaDados(i, novaPosX, novaPosY, estado2);
                    else
                        enviaDados(i, novaPosX, novaPosY, 0);
                }
            } while (!inputLine.equals(""));

            for (int i = 0; i < cont; i++)
                os[i].close(); // fecha todos os printstream
            is.close();
            clientSocket.close(); // fecha o cliente

        } catch (

        IOException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e) {
            System.out.println("Conexao terminada pelo cliente");
        }
    }
}

class MatrizMapa {
    public int matrizMapa[][] = {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
            { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
}