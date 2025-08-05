import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Conversor {

    // URL de la API con tu clave
    private static final String URL_API = "https://v6.exchangerate-api.com/v6/60c9503475b47130527d4d38/latest/USD";

    // Cliente HTTP para realizar las solicitudes
    private static HttpClient client = HttpClient.newHttpClient();

    // Scanner para leer entrada del usuario
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("*******************************************");
        System.out.println("Sea bienvenido/a al Conversor de Moneda =]");
        System.out.println("*******************************************");

        // Obtener tasas de cambio al inicio
        JsonObject tasasCambio = obtenerTasasCambio();
        if (tasasCambio == null) {
            System.out.println("Error: No se pudieron obtener las tasas de cambio");
            return;
        }

        exibirMenu(tasasCambio);
    }

    public static void exibirMenu(JsonObject tasasCambio) {
        int opcion = 0;

        do {
            System.out.println("\n*******************************************");
            System.out.println("1) Dólar =>> Peso argentino");
            System.out.println("2) Peso argentino =>> Dólar");
            System.out.println("3) Dólar =>> Real brasileño");
            System.out.println("4) Real brasileño =>> Dólar");
            System.out.println("5) Dólar =>> Peso colombiano");
            System.out.println("6) Peso colombiano =>> Dólar");
            System.out.println("7) Salir");
            System.out.println("Elija una opción válida:");
            System.out.println("*******************************************");

            try {
                opcion = scanner.nextInt();

                if (opcion >= 1 && opcion <= 6) {
                    realizarConversion(opcion, tasasCambio);
                } else if (opcion == 7) {
                    System.out.println("¡Gracias por usar el conversor!");
                } else {
                    System.out.println("Opción no válida. Intente nuevamente.");
                }

            } catch (Exception e) {
                System.out.println("Error: Ingrese un número válido");
                scanner.nextLine(); // Limpiar buffer
            }

        } while (opcion != 7);

        scanner.close();
    }

    public static JsonObject obtenerTasasCambio() {
        try {
            // Fase 5: Crear HttpRequest con configuración personalizada
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_API))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            // Fase 4: Usar HttpClient para enviar la solicitud
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            // Fase 6: Analizar HttpResponse
            if (response.statusCode() == 200) {
                System.out.println("✓ Conexión exitosa con la API");

                // Fase 7: Usar Gson con JsonParser para analizar JSON
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(response.body()).getAsJsonObject();

                // Verificar que la respuesta sea exitosa
                if (jsonResponse.get("result").getAsString().equals("success")) {
                    // Fase 8: Filtrar las monedas necesarias
                    return jsonResponse.getAsJsonObject("conversion_rates");
                } else {
                    System.out.println("Error en la respuesta de la API");
                    return null;
                }

            } else {
                System.out.println("Error HTTP: " + response.statusCode());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    public static void realizarConversion(int opcion, JsonObject tasasCambio) {
        System.out.print("Ingrese el valor que desea convertir: ");

        try {
            double valor = scanner.nextDouble();

            if (valor <= 0) {
                System.out.println("Error: El valor debe ser mayor a 0");
                return;
            }

            // Fase 9: Realizar la conversión según la opción
            double resultado = 0;
            String mensaje = "";

            switch (opcion) {
                case 1: // USD a ARS
                    resultado = valor * tasasCambio.get("ARS").getAsDouble();
                    mensaje = String.format("El valor %.2f [USD] corresponde al valor final de =>>> %.2f [ARS]",
                            valor, resultado);
                    break;

                case 2: // ARS a USD
                    resultado = valor / tasasCambio.get("ARS").getAsDouble();
                    mensaje = String.format("El valor %.2f [ARS] corresponde al valor final de =>>> %.2f [USD]",
                            valor, resultado);
                    break;

                case 3: // USD a BRL
                    resultado = valor * tasasCambio.get("BRL").getAsDouble();
                    mensaje = String.format("El valor %.2f [USD] corresponde al valor final de =>>> %.2f [BRL]",
                            valor, resultado);
                    break;

                case 4: // BRL a USD
                    resultado = valor / tasasCambio.get("BRL").getAsDouble();
                    mensaje = String.format("El valor %.2f [BRL] corresponde al valor final de =>>> %.2f [USD]",
                            valor, resultado);
                    break;

                case 5: // USD a COP
                    resultado = valor * tasasCambio.get("COP").getAsDouble();
                    mensaje = String.format("El valor %.2f [USD] corresponde al valor final de =>>> %.2f [COP]",
                            valor, resultado);
                    break;

                case 6: // COP a USD
                    resultado = valor / tasasCambio.get("COP").getAsDouble();
                    mensaje = String.format("El valor %.2f [COP] corresponde al valor final de =>>> %.2f [USD]",
                            valor, resultado);
                    break;
            }

            System.out.println("\n" + "=".repeat(60));
            System.out.println(mensaje);
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            System.out.println("Error: Ingrese un valor numérico válido");
            scanner.nextLine(); // Limpiar buffer
        }
    }
}