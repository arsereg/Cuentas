
import java.text.*;
import java.util.*;
import java.security.SecureRandom;

public class RutinasCuentas {
    //Defino formato de fecha
    static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    static final int CANTIDAD_MAXIMA_CLIENTES = 10000;
    static final int CANTIDAD_MAXIMA_TRANSACCIONES = 1000;
    static int[] cedula = new int[CANTIDAD_MAXIMA_CLIENTES];
    static int[] tel = new int[CANTIDAD_MAXIMA_CLIENTES];
    static String[][] userData = new String[CANTIDAD_MAXIMA_CLIENTES][5];
    static double[][] credito = new double[CANTIDAD_MAXIMA_CLIENTES][3];
    static int[] cantTransacciones = new int[CANTIDAD_MAXIMA_CLIENTES];
    static String[][] transacciones = new String[CANTIDAD_MAXIMA_CLIENTES][CANTIDAD_MAXIMA_CLIENTES];
    static boolean[] cuentasBloqueadas = new boolean[CANTIDAD_MAXIMA_CLIENTES];
    static int cantClientesRegistrados = 0;
    static double comisionCredito = 0.01;
    private static String cuentaLogueada = "";
    private static String[] encryptionPass = new String[CANTIDAD_MAXIMA_CLIENTES];
    static boolean sistemaInicializado = false;
    static boolean administradorLogueado = false;
    static boolean modoPrueba = false;
    private static String encryptPass;

    static void registrarCuenta(int pcedula, String pnombre, String papellido, int ptelefono, double pcredito) throws java.io.IOException {
        Calendar cal = Calendar.getInstance();
        int indice = buscarClienteCedula(pcedula);
        if (indice == -1) {
            int i = cantClientesRegistrados;
            String encriptador = generarTextoAleatorio();
            encryptionPass[i] = encriptador;
            cantTransacciones[i] = 0;
            cedula[i] = pcedula;
            userData[i][4] = dateFormat.format(cal.getTime());
            userData[i][3] = papellido;
            userData[i][2] = pnombre;
            userData[i][1] = crearContrasenia(pnombre, papellido, pcedula, encryptionPass[i]);
            userData[i][0] = "cta-" + (i + 1);
            tel[i] = ptelefono;
            transacciones[i][cantTransacciones[i]] = cantTransacciones[i] + "." + userData[i][4] + " CREACION DE CUENTA";
            credito[i][0] = pcredito;
            credito[i][1] = pcredito;
            credito[i][2] = 0;
            cantTransacciones[i]++;
            cantClientesRegistrados++;
        }
    }

    static int buscarClienteCedula(int pcedula) {
        int i = 0;
        int indice = -1;
        boolean encontrado = false;
        while (i < cantClientesRegistrados && !encontrado) {
            if (pcedula == cedula[i]) {
                encontrado = true;
                indice = i;
            } else {
                i++;
            }
        }
        return indice;
    }

    static int buscarClienteNumCuenta(String pnumCuenta) {
        int i = 0;
        int indice = -1;
        boolean encontrado = false;
        while (i < cantClientesRegistrados && !encontrado) {
            if (pnumCuenta.equals(userData[i][0])) {
                encontrado = true;
                indice = i;
            } else {
                i++;
            }
        }
        return indice;
    }
    
    static String obtenerNumCuenta(int pindice){
        String resultado = userData[pindice][0];
        return resultado;
    }

    static String crearContrasenia(String pnombre, String papellido, int pcedula, String pencriptionPass) {
        int indice = cantClientesRegistrados;
        pnombre = pnombre.toUpperCase();
        papellido = papellido.toLowerCase();
        String userCedula = "" + pcedula;
        char trozoUno = pnombre.charAt(0);
        char trozoDos = papellido.charAt(0);
        char trozoTres = userCedula.charAt(0);
        char trozoCuatro = userCedula.charAt(1);
        String pass = "" + trozoUno + trozoDos + trozoTres + trozoCuatro;
        AES.setKey(encryptionPass[indice]);
        AES.encrypt(pass);
        String resultado = AES.getEncryptedString();
        return resultado;
    }

    static String verDatosCliente(String pnumCuenta) {
        int indice = buscarClienteNumCuenta(pnumCuenta);
        String resultado = "";
        String encryptPass = encryptionPass[indice];
        AES.setKey(encryptPass);
        AES.decrypt(userData[indice][1]);
        String saved = userData[indice][1];
        String pass = AES.getDecryptedString();
        if (indice != -1) {
            resultado = "Nombre: " + userData[indice][2] + "\nApellido: " + userData[indice][3] + "\nTelefono: " + tel[indice] + "\nCedula: " + cedula[indice] + "\nFecha de creacion: " + userData[indice][4] + "\nNumero de cuenta: " + userData[indice][0] + "\nLimite de credito: " + credito[indice][0] + "\nCredito disponible: " + credito[indice][1] + "\nPassword (Encriptado): " + saved + "\nPassword (desencriptado):" + pass + "\n*******************************************************\n";
        }
        return resultado;

    }

    static boolean verificarUsuario(String pnumCuenta, String pcontrasenia) {
        String pass = solicitarEncriptionPass(pnumCuenta);
        AES.setKey(pass);
        AES.decrypt(pcontrasenia.trim());
        String contrasenia = AES.getDecryptedString();
        boolean resultado = false;
        int indice = buscarClienteNumCuenta(pnumCuenta);
        if (indice != -1) {
            AES.setKey(encryptionPass[indice]);
            AES.decrypt(userData[indice][1]);
            String validUserPasswordDecrypted = AES.getDecryptedString();
            if (contrasenia.equals(validUserPasswordDecrypted)) {
                resultado = true;
                cuentaLogueada = pnumCuenta;
                
            }
        }
        return resultado;
    }

    static boolean cambiarContrasenia(String[] pcontrasenias) {
        boolean resultado = false;
        String oldPass = pcontrasenias[0];
        String newPass = pcontrasenias[1];
        AES.decrypt(oldPass.trim());
        oldPass = AES.getDecryptedString();
        AES.decrypt(newPass.trim());
        newPass = AES.getDecryptedString();
        int indice = buscarClienteNumCuenta(cuentaLogueada);
        String newEncriptionPass = solicitarEncriptionPass(cuentaLogueada);
        AES.setKey(newEncriptionPass);
        AES.encrypt(oldPass);
        boolean usuarioVerificado = verificarUsuario(cuentaLogueada, AES.getEncryptedString());
        if(usuarioVerificado) {
            userData[indice][1] = newPass;
            resultado = true;
        }
        return resultado;
    }

    static String retirarMonto(double pretiro) {
        Calendar cal = Calendar.getInstance();
        String resultado;
        int indice = buscarClienteNumCuenta(cuentaLogueada);
        if (pretiro <= credito[indice][1]) {
            double comision = credito[indice][0] / 100;
            double total = pretiro + comision;
            credito[indice][2] += total;
            credito[indice][1] = credito[indice][1] - total;
            resultado = "Se ha realizado el retiro de " + pretiro + " colones correctamente\n***\n";
            transacciones[indice][cantTransacciones[indice]] = cantTransacciones[indice] + "." + dateFormat.format(cal.getTime()) + " Retiro -> " + pretiro;
            cantTransacciones[indice] = cantTransacciones[indice] + 1;
        }else{
            resultado = "No se ha completado el retiro. Verifique que tiene fondos suficientes y vuelva a intentar";
        }

        return resultado;
    }

    static String pagarTarjeta(double ppago) {
        int indice = buscarClienteNumCuenta(cuentaLogueada);
        double adeudado = credito[indice][2];
        String resultado;
        if(ppago <= adeudado){
            Calendar cal = Calendar.getInstance();
            double pagoMinimo = 5000.0;
            resultado = "No se ha realizado el pago. Por favor ingrese mas de " + pagoMinimo + " colones. Si adeuda menos de 5000 colones, por favor ingrese la cantidad exacta para pagar.";
            double tasaInteres = 0.05;
            if (ppago >= pagoMinimo || ppago == adeudado) {
                credito[indice][1] += ppago;
                credito[indice][2] -= ppago;
                resultado = "Se ha recibido un pago por " + ppago + " colones.";
                transacciones[indice][cantTransacciones[indice]] = cantTransacciones[indice] + "." + dateFormat.format(cal.getTime()) + " Pago -> " + ppago;
                cantTransacciones[indice] = cantTransacciones[indice] + 1;
                if (credito[indice][2] > 0) {
                    adeudado =  credito[indice][2] * tasaInteres; 
                    credito[indice][2] = credito[indice][2] + adeudado;
                }
                if(credito[indice][1] > credito[indice][0]){
                    credito[indice][1] = credito[indice][0];
                }
            }
        }else{
            resultado = "El pago no puede exceder el monto adeudado";
        }
        return resultado;

    }

    static String consultarTransaccion(int ptransaccion) {
        int indice = buscarClienteNumCuenta(cuentaLogueada);
        String resultado = transacciones[indice][ptransaccion];
        return resultado;
    }
    
    static double[] consultarSaldos(){
        int indice = buscarClienteNumCuenta(cuentaLogueada);
        double limCredito = credito[indice][0];
        double disponible = credito[indice][1];
        double adeudado = credito[indice][2];
        double[] resultado = {limCredito, adeudado, disponible};
        return resultado;
    }

    static String comprarArticulo(Double pcompra) {
        String resultado;
        Calendar cal = Calendar.getInstance();
        int indice = buscarClienteNumCuenta(cuentaLogueada);
        if (pcompra < credito[indice][1]) {
            credito[indice][1] -=  pcompra;
            credito[indice][2] += pcompra;
            resultado = "Se ha realizado una compra de " + pcompra + " colones correctamente\n***\n";
            transacciones[indice][cantTransacciones[indice]] = cantTransacciones[indice] + "." + dateFormat.format(cal.getTime()) + " Compra -> " + pcompra;
            cantTransacciones[indice] = cantTransacciones[indice] + 1;
        }else{
            resultado = "No se ha completado la compra. Verifique que tiene fondos suficientes y vuelva a intentar";
        }
        return resultado;
    }

    static void bloquearCuenta(int pindice) {
        cuentasBloqueadas[pindice] = true;
    }
    
    static boolean validarClaveAdmin (String pclave){
        AES.setKey(encryptPass);
        AES.decrypt(pclave);
	String adminPassword = "startrek";
	boolean valido= false;
	String clave = AES.getDecryptedString();
	if(adminPassword.equals(clave)){
		valido = true;
	}
	return valido; 
    } 
    
    static void cambiarDatos(int pindice, String pname, String plastName, int pced, int pphone, String pcuenta, String ppassword){
        if(pced != -1){
            cedula[pindice] = pced;
        }
        if(!pname.equals("")){
            userData[pindice][2] = pname;
        }

        if(!plastName.equals("")){
            userData[pindice][3] = plastName;
        }
        if(pphone != -1){
            tel[pindice] = pphone;
        }
        if(!pcuenta.equals("-0")){
            userData[pindice][0] = pcuenta;
        }
        if(!ppassword.equals("-0")){
            userData[pindice][1] = ppassword;
        }
    }
    
    static boolean verificarCuentaBloqueada(int pindice){
        boolean resultado = cuentasBloqueadas[pindice];
        return resultado;
    }
    
    static void desbloquearCuenta(int pindice){
        cuentasBloqueadas[pindice] = false;
    }

    static String generarTextoAleatorio(){
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom rnd = new SecureRandom();
        StringBuilder resultado = new StringBuilder( 5 );
        for( int i = 0; i < 5; i++ ){ 
           resultado.append( letras.charAt( rnd.nextInt(letras.length()) ) );
        }
        return resultado.toString();
    }
    
    static int generarNumeroAleatorio(){
        int rnd = (int) (Math.random() * 1000000000);
        return rnd;
    }
    
    static void iniciarSistema(){
        sistemaInicializado = true;
    }
    
    static boolean verificarSistemaIniciado(){
        boolean resultado = sistemaInicializado;
        return resultado;
    }
    
    static void rellenarSistema(){
        int CANT_CUENTAS_PARA_RELLENAR = 5000;
        modoPrueba = true;
        for(int i = 0; i < CANT_CUENTAS_PARA_RELLENAR; i++){
            String encriptador = generarTextoAleatorio();
            encryptionPass[i] = encriptador;
            Calendar cal = Calendar.getInstance();
            cantTransacciones[i] = 0;
            cedula[i] = generarNumeroAleatorio();
            userData[i][2] = generarTextoAleatorio();
            userData[i][3] = generarTextoAleatorio();
            tel[i] = generarNumeroAleatorio();
            userData[i][4] = dateFormat.format(cal.getTime());
            transacciones[i][cantTransacciones[i]] = cantTransacciones[i] + "." + userData[i][4] + "CREACION DE CUENTA";
            userData[i][0] = "cta-" + (i + 1);
            userData[i][1] = crearContrasenia(userData[i][2], userData[i][3], cedula[i], encryptionPass[i]);
            credito[i][0] = generarNumeroAleatorio()/1000;
            credito[i][1] = credito[i][0];
            cantClientesRegistrados++;
        }
    }
    
    static void reiniciarSistema(){
        cedula = new int[CANTIDAD_MAXIMA_CLIENTES];
        tel = new int[CANTIDAD_MAXIMA_CLIENTES];
        userData = new String[CANTIDAD_MAXIMA_CLIENTES][5];
        credito = new double[CANTIDAD_MAXIMA_CLIENTES][3];
        cantTransacciones = new int[CANTIDAD_MAXIMA_CLIENTES];
        transacciones = new String[CANTIDAD_MAXIMA_CLIENTES][CANTIDAD_MAXIMA_TRANSACCIONES];
        cuentasBloqueadas = new boolean[CANTIDAD_MAXIMA_CLIENTES];
        cantClientesRegistrados = 0;
        cuentaLogueada = "";
        sistemaInicializado = false;
        modoPrueba = false;
    }
    
    static String verEstadoSistema(){
        String resultado = "";
        if(cantClientesRegistrados > 0){
            for(int i = 0; i < cantClientesRegistrados; i++){
                String cuenta = obtenerNumCuenta(i);
                String datosCliente = verDatosCliente(cuenta);
                resultado += datosCliente;
            }
        }else{
            resultado = "No hay clientes registrados en el sistema.";
        }
        return resultado;
    }
    
    static void loguearAdministrador(){
        administradorLogueado = true;
    }
    
    static void desloguearAdministrador(){
        administradorLogueado = false;
    }
    
    static int contarClientesRegistrados(){
        int resultado = cantClientesRegistrados;
        return resultado;
    }
    
    static int consultarCantidadMaximaClientes(){
        int resultado = CANTIDAD_MAXIMA_CLIENTES;
        return resultado;
    }
    
    static boolean verificarEstadoAdministrador(){
        boolean resultado = administradorLogueado;
        return resultado;
    }
    
    static int consultarCantidadTransacciones(){
        int indice = buscarClienteNumCuenta(cuentaLogueada);
        int resultado = cantTransacciones[indice];
        return resultado;
    }
    
    static int consultarCantidadClientesRegistrados(){
        int resultado = cantClientesRegistrados;
        return resultado;
    }
    
    static boolean verificarModoPrueba(){
        boolean resultado = modoPrueba;
        return resultado;
    }
    
    static String solicitarEncriptionPass(String pnumCuenta){
        int indice = buscarClienteNumCuenta(pnumCuenta);
        String pass = encryptionPass[indice];
        return pass;
    }
    
    static String generarEncryptionPass(){
        String resultado = generarTextoAleatorio();
        encryptPass = resultado;
        return resultado;
    }

}
