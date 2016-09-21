import java.io.*;

public class IUCuentas{
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static PrintStream out = System.out;

    public static void main(String[] args) throws java.io.IOException {
        intentarArranque();
    }
    
    static void intentarArranque()throws java.io.IOException{
        
        try{
            arrancar();
        }catch(java.lang.NumberFormatException arranque){
            out.println(arranque.getMessage());
            intentarArranque();
        }
    }
    
    static void arrancar()throws java.io.IOException{
    int accion;
        do {
            mostrarMenuPrincipal();
            out.print("Digite el numero de menu al que desea accesar: ");
            accion = Integer.parseInt(in.readLine());
            switch (accion) {
                case 1:
                    boolean activo = iniciarSesion();
                    if(activo){
                        do {
                            mostrarMenuUsuario();
                            out.print("Digite el numero de menu al que desea accesar: ");
                            accion = Integer.parseInt(in.readLine());
                            switch (accion) {
                                case 1:
                                    double retiro = solicitarMontoRetiro();
                                    realizarRetiro(retiro);
                                    break;
                                case 2:
                                    double pago = solicitarMontoPago();
                                    String resultadoPagarTarjeta = RutinasCuentas.pagarTarjeta(pago);
                                    out.println(resultadoPagarTarjeta);
                                    break;
                                case 3:
                                    comprar();
                                    break;
                                case 4: 
                                    mostrarSaldos();
                                    break;
                                case 5:
                                    consultarEstadoCuenta();
                                    break;
                                case 6:
                                    cambiarPass();
                                    break;
                                case 7:
                                    guardarSaldos();
                                    break;
                                case 8:
                                    guardarEstadoCuenta();
                                    break;
                                case 9:
                                    cerrarSesionUsuario();
                                    break;
                            }
                        } while (accion != 9);
                    }
                    break;
                case 2:
                    boolean validAdmin = iniciarAdmin();
                    if(validAdmin){
                        do {
                            mostrarMenuAdministrador();
                            out.print("Digite el numero de menu al que desea accesar: ");
                            accion = Integer.parseInt(in.readLine());
                            switch (accion) {
                                case 1:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        if(obtenerClientesRegistrados() < obtenerMaxCantClientes()){
                                            registrarUsuario();
                                        }else{
                                            out.println("La base de datos esta llena. No se pueden rellenar mas usuarios.");
                                        }
                                        
                                    }
                                    break;
                                case 2:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        modificarDatos();
                                    }else{
                                        mostrarErrorAdminPass();
                                    }
                                    break;
                                case 3:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        consultarCliente();
                                    }else{
                                        mostrarErrorAdminPass();
                                    }
                                    break;
                                case 4:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        RutinasCuentas.loguearAdministrador();
                                    }else{
                                        mostrarErrorAdminPass();
                                    }
                                    break;
                                case 5:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        liberarCuenta();
                                    }else{
                                        mostrarErrorAdminPass();
                                    }
                                    break;
                                case 6:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        boolean sistemaModoPrueba = confirmarModoPrueba();
                                        if(!sistemaModoPrueba){
                                            probarSistema();
                                        }else{
                                            out.println("El sistema ya esta corriendo el modo de prueba.");
                                        }
                                    }else{
                                        mostrarErrorAdminPass();
                                    }
                                    break;
                                case 7:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        limpiarSistema();
                                    }else{
                                        mostrarErrorAdminPass();
                                    }
                                    break;
                                case 8:
                                    if(verificarEstadoAdministrador() || iniciarAdmin()){
                                        String estadoSistema = RutinasCuentas.verEstadoSistema();
                                        out.print(estadoSistema);
                                    }else{
                                        mostrarErrorAdminPass();
                                    }
                                    break;
                                case 9:
                                    desloguearAdmin();
                                    break;
                            }
                        } while (accion != 9);
                    }else{
                        mostrarErrorAdminPass();
                    }
                    break;
                case 3:
                    out.println("Gracias por usar nuestro sistema.");
                    out.println("Hasta pronto");
                    break;
                default:
                    out.println("Opcion invalida.");
                    break;
            }
        } while (accion != 3);
    }

    static void registrarUsuario() throws java.io.IOException {
        out.println("***************************");
        out.println("REGISTRO DE NUEVO USUARIO");
        out.println("***************************");
        out.println();
        out.print("Nombre: ");
        String userName = in.readLine();
        out.print("Apellido: ");
        String lastName = in.readLine();
        int clientId = 0;
        do{
            try{
                int indice;
                do{
                out.print("Numero de cedula: ");
                clientId = Integer.parseInt(in.readLine());
                indice = RutinasCuentas.buscarClienteCedula(clientId);
                if(indice != -1){
                    out.println("Ese cliente ya está registrado. Por favor ingrese un nuevo de identificacion diferente");
                }
                }while(indice != -1);
            }catch(java.lang.NumberFormatException cedula){
                out.println("Error, vuelva a intentarlo");
            }
            if(clientId < 100){
                out.println("La cedula debe tener al menos 3 digitos");
            }
        }while(clientId < 100);
        int clientPhone = solicitarTel();
        double credit = solicitarMontoCredito(userName);
        RutinasCuentas.registrarCuenta(clientId, userName, lastName, clientPhone, credit);
        boolean modoDePrueba = confirmarModoPrueba();
        if(!modoDePrueba){
            RutinasCuentas.iniciarSistema();
        }
    }
    
    static boolean confirmarModoPrueba(){
        boolean resultado = RutinasCuentas.verificarModoPrueba();
        return resultado;
    }
        
    static int solicitarTel()throws java.io.IOException{
        out.println("Telefono: ");
        int resultado;
        int telefono;
        try{
           telefono = Integer.parseInt(in.readLine());
           resultado = telefono;
        }catch(java.lang.NumberFormatException errorTel){
            resultado = solicitarTel();
        }
        return resultado;
    }
    
    static double solicitarMontoCredito(String puserName)throws java.io.IOException{
        double resultado;
        try{
            out.print("Cual es el credito de " + puserName + ": ");
            String userInput = in.readLine();
            if(userInput.equals("")){
                resultado = 100000;
            }else{
                resultado = Double.parseDouble(userInput);
            }
        }catch(java.lang.NumberFormatException credito){
            out.println(credito.getMessage());
            resultado = solicitarMontoCredito(puserName);
        }
        return resultado;
    }

    static boolean iniciarSesion() throws java.io.IOException {
        int intento = 0;
        int CANT_MAX_INTENTOS = 3;
        boolean resultado = false;
        int indice = -1;
        String userAccount = "";
        int cantidadClientesRegistrados = obtenerClientesRegistrados();
        if(cantidadClientesRegistrados > 0){
            do{
                out.print("Numero de cuenta: (Digite 0 para salir)");
                userAccount = in.readLine();
                indice = RutinasCuentas.buscarClienteNumCuenta(userAccount);
                if(indice == -1){
                    out.println("Ese numero de cuenta no existe");
                }
            }while(indice == -1 && !userAccount.equals("0"));
            
            boolean cuentaBloqueada = RutinasCuentas.verificarCuentaBloqueada(indice);
            if(cuentaBloqueada == true){
                out.println("Esta cuenta esta bloqueada. Por favor contacte a un administrador");
            }else{
                do {
                    out.print("Contrasenia: ");
                    String userPassword = in.readLine();
                    AES.setKey(RutinasCuentas.solicitarEncriptionPass(userAccount));
                    AES.encrypt(userPassword.trim());
                    resultado = RutinasCuentas.verificarUsuario(userAccount, AES.getEncryptedString());
                    if(!resultado){
                        intento++;
                    }
                    if (intento == CANT_MAX_INTENTOS) {
                        RutinasCuentas.bloquearCuenta(indice);
                        out.println("Esta cuenta se ha bloqueado. Para desbloquearla contacte a un administrador");
                    }
                } while (intento < CANT_MAX_INTENTOS && !resultado);
            }
            
        }else{
            out.println("No hay clientes registrados");
        }
        return resultado;
    }

    static void mostrarMenuPrincipal() {
        out.println();
        out.println("1. Cuentas");
        out.println("2. Administrador");
        out.println("3. Salir");
        out.println();
    }

    static void mostrarMenuUsuario() {
        out.println();
        out.println("******");
        out.println("MENU DE USUARIO");
        out.println("******");
        out.println();
        out.println("1. Retirar Monto");
        out.println("2. Pagar Tarjeta");
        out.println("3. Comprar Articulo");
        out.println("4. Consultar Saldos");
        out.println("5. Consultar Transacciones");
        out.println("6. Cambiar Clave");
        out.println("7. Guardar Saldos");
        out.println("8. Guardar Historico de Transacciones");
        out.println("9. Salir");
        out.println();
    }

    static void mostrarMenuAdministrador() {
        out.println();
        out.println("******");
        out.println("MENU DE ADMINISTRADOR");
        out.println("******");
        out.println();
        out.println("1. Registrar una cuenta");
        out.println("2. Modificar los datos de un cliente");
        out.println("3. Consultar por un cliente");
        out.println("4. Deshabilitar temporalmente la clave del administrador");
        out.println("5. Liberar una cuenta bloqueada");
        out.println("6. Probar Sistema");
        out.println("7. Reiniciar Sistema");
        out.println("8. Ver estado del sistema");
        out.println("9. Salir");
        out.println();
    }
    
    static double solicitarMontoRetiro()throws java.io.IOException{
        double retiro;
        try{
            do{
            out.print("Cuanto desea retirar?");
                retiro = Double.parseDouble(in.readLine());
                if(retiro <=0){
                    out.println("Por favor digite un numero mayor a 0");
                }
            }while(retiro < 0);
        }catch(java.lang.NumberFormatException errorRetiro){
            out.println("Ha ingresado un valor invalido. Por favor ingrese solo numeros validos");
            retiro = solicitarMontoRetiro();
        }
        
        double resultado = retiro;
        return resultado;
    }
    
    static double solicitarMontoPago()throws java.io.IOException{
    double pago;
    try{
        do{
            out.print("Cuanto desea abonar a su deuda?");
            pago = Double.parseDouble(in.readLine());
            if(pago < 0){
                out.println("El pago no puede ser de numeros negativos");
            }
        }while(pago <= 0);
    }catch(java.lang.NumberFormatException errorPago){
        pago = solicitarMontoPago();
    }
    double resultado = pago;
    return resultado;
    }
    
    
    static void mostrarSaldos()throws java.io.IOException{
        double[] saldos = RutinasCuentas.consultarSaldos();
        double saldoLimite = saldos[0];
        double saldoAdeudado = saldos[1];
        double saldoDisponible = saldos[2];
        if(saldoDisponible < 0){
            saldoDisponible = 0;
        }
        out.println("Saldo Limite: " + saldoLimite);
        out.println("Adeudado: " + saldoAdeudado);
        out.println("Disponible para compras o retiros: " + saldoDisponible);
    }
    
    static void guardarSaldos()throws java.io.IOException{
        double[] saldos = RutinasCuentas.consultarSaldos();
        double saldoLimite = saldos[0];
        double saldoAdeudado = saldos[1];
        double saldoDisponible = saldos[2];
        out.println("Saldo Limite: " + saldoLimite);
        out.println("Adeudado: " + saldoAdeudado);
        out.println("Disponible para compras o retiros: " + saldoDisponible);
        out.println("Digite el nombre del archivo para guardar los saldos");
        String fileName = in.readLine();
        fileName = fileName + " (SALDOS).txt";
        File f = new File (fileName);
        if(f.exists()){
            out.println("El archivo ya existe. Desea sobreescribirlo?");
            out.println("Y - N");
            String accion = in.readLine();
            if(accion.equals("Y") || accion.equals("y")){
                PrintWriter writer = new PrintWriter(fileName, "UTF-8");
                writer.println("Saldo Limite: " + saldoLimite);
                writer.println("Adeudado: " + saldoAdeudado);
                writer.println("Disponible para compras o retiros: " + saldoDisponible);
                writer.close();
                out.println("El archivo se ha escrito correctamente.");
            }else{
                out.println("Archivo no guardado");
            }
        }else{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println("Saldo Limite: " + saldoLimite);
            writer.println("Adeudado: " + saldoAdeudado);
            writer.println("Disponible para compras o retiros: " + saldoDisponible);
            writer.close();
            out.println("El archivo se ha escrito correctamente.");
        }
        
    }
    
    static void cambiarPass()throws java.io.IOException{
        out.print("Digite su contrasenia actual:");
        String passActual = in.readLine();
        out.println("Digite la nueva contrasenia");
        String passNuevo = in.readLine();
        out.println("Digite la nueva contrasenia otra vez");
        String passNuevoVerificacion = in.readLine();
        if(passNuevo.equals(passNuevoVerificacion)){
            String key = RutinasCuentas.generarEncryptionPass();
            AES.setKey(key);
            AES.encrypt(passActual);
            String actualEncrypt = AES.getEncryptedString();
            AES.encrypt(passNuevo);
            String newEncrypt = AES.getEncryptedString();
            String[] paqueteContrasenias = {actualEncrypt, newEncrypt};
            boolean cambioRealizado = RutinasCuentas.cambiarContrasenia(paqueteContrasenias);
            if(cambioRealizado == true){
                out.println("Contrasenia cambiada");
            }else{
                out.println("No introdujo la contrasenia actual correctamente. Por favor intentelo de nuevo");
            }
        }else{
            out.println("Las contrasenias no coinciden.");
        }
    }   

    static void comprar()throws java.io.IOException{
        double compra;
        do{
            compra = solicitarCompra();
            if(compra > 0){
                String resultado = RutinasCuentas.comprarArticulo(compra);
                out.println(resultado);
            }
        }while(compra != 0);
    }
    
    static double solicitarCompra()throws java.io.IOException{
        double compra;
        try{
            out.println("Introduzca el precio del articulo (Digite 0 para terminar de acumular productos en la factura)");
                do{
                    compra = Double.parseDouble(in.readLine());
                    if(compra < 0){
                        out.println("Error");
                        out.println("No se puede realizar compras con valor negativo. Por favor ingrese un valor positivo");
                    }
                }while(compra < 0);
        }catch(java.lang.NumberFormatException errorCompra){
            compra = solicitarCompra();
        }
        double resultado = compra;
        return resultado;
    }
    
    static void modificarDatos()throws java.io.IOException{
        out.println("*********");
        out.println("MODIFICACION DE DATOS DE CLIENTE");
        out.println("*********");
        out.println("");
        out.print("Ingrese el numero de cuenta del cliente: ");
        String numCuenta = in.readLine();
        int indice = RutinasCuentas.buscarClienteNumCuenta(numCuenta);
        if(indice == -1){
            out.println("Ese numero de cuenta no existe");
        }else{
            String datosCliente = RutinasCuentas.verDatosCliente(numCuenta);
            out.println(datosCliente);
            out.println("Desea cambiar los datos de este cliente?");
            out.println("Y- Si");
            out.println("N- No");
            String accion = in.readLine();
            if(accion.equals("y")){
                
                out.println("Desea cambiar el nombre del cliente?");
                out.println("Y - N");
                accion = in.readLine();
                String name = "";
                if( accion.equals("Y") || accion.equals("y") ){
                    out.println("Introduzca el nuevo nombre");
                    name = in.readLine();
                }
                
                out.println("Desea cambiar el apellido del cliente? ");
                out.println("Y - N");
                accion = in.readLine();
                String lastName = "";
                if( accion.equals("Y") || accion.equals("y") ){
                    out.println("Introduzca el nuevo apellido");
                    lastName = in.readLine();
                }
                
                out.println("Desea cambiar el numero de cedula del cliente?");
                out.println("Y - N");
                accion = in.readLine();
                int ced = 0;
                if( accion.equals("Y") || accion.equals("y") ){
                    int posicionCliente = 0;
                    do{
                        out.println("Introduzca la nueva cedula:");
                        ced = Integer.parseInt(in.readLine());
                        if(ced < 100){
                            out.println("La cedula debe tener al menos 3 digitos");
                        }
                        posicionCliente = RutinasCuentas.buscarClienteCedula(ced);
                        if(posicionCliente != -1){
                            out.println("Ese cliente ya esta registrado en el sistema");
                        }
                    }while(ced < 100 || posicionCliente != -1);
                }
                
                out.println("Desea cambiar el numero de telefono del cliente?");
                out.println("Y - N");
                accion = in.readLine();
                int phone = 0;
                if( accion.equals("Y") || accion.equals("y") ){
                    out.println("Telefono: ");
                    phone = Integer.parseInt(in.readLine());
                }                    
                out.println("Desea cambiar el numero de cuenta para el cliente");
                out.println("Y - N");
                accion = in.readLine();
                String numeroCuenta;
                if( accion.equals("Y") || accion.equals("y") ){
                    out.println("Escriba el nuevo numero de cuenta");
                    numeroCuenta = in.readLine();
                }else{
                    numeroCuenta = "-0";
                }
                String contrasenia;
                out.println("Desea cambiar la contrasenia para el cliente");
                out.println("Y - N");
                if( accion.equals("Y") || accion.equals("y") ){
                    out.println("Escriba la nueva Contrasenia");
                    contrasenia = in.readLine();
                }else{
                    contrasenia = "-0";
                }
                
                RutinasCuentas.cambiarDatos(indice, name, lastName, ced, phone, numeroCuenta, contrasenia);
            }
            
        }
    }
    
     static void consultarCliente()throws java.io.IOException{
        String resultado = "Ese numero de cuenta no existe";
        out.println("Digite el numero de cuenta del cliente");
        String numCuenta = in.readLine();
        int indice = RutinasCuentas.buscarClienteNumCuenta(numCuenta);
        if(indice != -1){
            resultado = RutinasCuentas.verDatosCliente(numCuenta);
        }
        out.println(resultado);
    }
     
    static void liberarCuenta()throws java.io.IOException{
        out.println("Digite el numero de cuenta que desea liberar");
        String numCuenta = in.readLine();
        int indice = RutinasCuentas.buscarClienteNumCuenta(numCuenta);
        if(indice != -1){
            boolean cuentaBloqueada = RutinasCuentas.verificarCuentaBloqueada(indice);
            if(cuentaBloqueada == true){
                out.println("Desea liberar la cuenta " + numCuenta + "?");
                out.println("Y-N");
                String accion = in.readLine();
                if(accion.equals("Y") || accion.equals("y")){
                    RutinasCuentas.desbloquearCuenta(indice);
                }
            }else{
                out.println("La cuenta no esta bloqueada");
            }
        }else{
            out.println("Esta cuenta no existe");
        }
    }
    
    static void probarSistema(){
        boolean sistemaIniciado = RutinasCuentas.verificarSistemaIniciado();
        if(sistemaIniciado){
            out.println("El sistema ya está iniciado, no se puede correr la prueba una vez se hayan ingresado datos válidos");
        }else{
            out.println("Se van a generar datos falsos para cada una de las cuentas. Por favor espere.");
            RutinasCuentas.rellenarSistema();
            out.println("////////////////////////////////////////");
            out.println("ALERTA");
            out.println("///////////////////////////////////////");
            out.println("Se ha llenado el sistema con información aleatoria.");
            out.println("Una vez haya finalizado las pruebas, por favor reinicie el sistema");
        }
    }
    
    static void limpiarSistema(){
        boolean sistemaIniciado = RutinasCuentas.verificarSistemaIniciado();
        if(!sistemaIniciado){
            RutinasCuentas.reiniciarSistema();
            out.println("Sistema reiniciado con exito");
        }else{
            out.println("No se puede reiniciar el sistema si hay datos de clientes creados");
        }
    }
    
    static boolean verificarEstadoAdministrador(){
        boolean resultado = RutinasCuentas.verificarEstadoAdministrador();
        return resultado;
    }
    
    static boolean iniciarAdmin()throws java.io.IOException{
        String pass = solicitarPassAdmin();
        String key = RutinasCuentas.generarEncryptionPass();
        AES.setKey(key);
        AES.encrypt(pass);
        boolean resultado = RutinasCuentas.validarClaveAdmin(AES.getEncryptedString());
        return resultado;              
    }
    
    static String solicitarPassAdmin()throws java.io.IOException{
        out.println("Introduzca la clave de administrador");
        String pass = in.readLine();
        String resultado = pass;
        return resultado;
    }
    
    static void desloguearAdmin(){
        RutinasCuentas.desloguearAdministrador();
    }
    
    static int obtenerClientesRegistrados(){
        int resultado = RutinasCuentas.contarClientesRegistrados();
        return resultado;
    }
    
    static int obtenerMaxCantClientes(){
        int resultado = RutinasCuentas.consultarCantidadMaximaClientes();
        return resultado;
    }
    
    static void guardarEstadoCuenta()throws java.io.IOException{
        int cantTransacciones = RutinasCuentas.consultarCantidadTransacciones();
        for(int i = 0; i < cantTransacciones; i++){
            out.println(RutinasCuentas.consultarTransaccion(i));
        }
        out.println("Digite el nombre del archivo para guardar el historico transaccional");
        String fileName = in.readLine() + " (TRANSACCIONES).txt";
        File f = new File(fileName);
        if(f.exists()){
            out.println("Ese archivo ya existe. Desea reemplazarlo?");
            out.println("Y - N");
            String accion = in.readLine();
            if(accion.equals("Y") || accion.equals("y")){
                PrintWriter writer = new PrintWriter(fileName, "UTF-8");
                for(int i = 0; i < cantTransacciones; i++){
                    writer.println(RutinasCuentas.consultarTransaccion(i));
                }
                writer.close();
                out.println("Archivo escrito con exito");
            }else{
                out.println("Archivo no escrito.");
            }
        }else{
            try{
                PrintWriter writer = new PrintWriter(fileName, "UTF-8");
                for(int i = 0; i < cantTransacciones; i++){
                    writer.println(RutinasCuentas.consultarTransaccion(i));
                }
                writer.close();
                out.println("Archivo escrito con exito");
            }catch(Exception errorEscritura){
                out.println("Algo ocurrió y no se pudo guardar el archivo");
                out.println(errorEscritura.getMessage());
            }
        }
    }
    
    static void consultarEstadoCuenta()throws java.io.IOException{
        int cantTransacciones = RutinasCuentas.consultarCantidadTransacciones();
        for(int i = 0; i < cantTransacciones; i++){
            out.println(RutinasCuentas.consultarTransaccion(i));
        }
    }
    
    static boolean revisarModoPrueba(){
        boolean resultado = RutinasCuentas.verificarModoPrueba();
        return resultado;
    }
    
    static void mostrarErrorAdminPass(){
        String msj = "Contrasenia de administrador erronea";
        out.println(msj);
    }
    
    static void realizarRetiro(double pretiro){
        String transaccion = RutinasCuentas.retirarMonto(pretiro);
        out.println(transaccion);
    }
    
    static void cerrarSesionUsuario()throws java.io.IOException{
        out.println("Se va a cerrar su cuenta. Desea guardar los archivos?");
        out.println("Y - N");
        String accion = in.readLine();
        if(accion.equals("y") || accion.equals("Y")){
            guardarEstadoCuenta();
            guardarSaldos();
        }
    }

}