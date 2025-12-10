/*
 * Clase: MainApp
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * Descripción: Punto de entrada de la aplicación. Inicializa los repositorios,
 * servicios y controladores, luego lanza la ventana principal del sistema.
 */

package main;

import javax.swing.SwingUtilities;
import view.MainWindow;
import controller.AuthController;
import controller.PublicacionController;
import controller.AdminController;
import controller.ReporteController;
import service.UserService;
import service.PublicacionService;
import service.OfertaService;
import service.AdminService;
import service.ReporteService;
import persistence.UserRepository;
import persistence.PublicacionRepository;
import persistence.OfertaRepository;
import persistence.ReporteRepository;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Repositorios
            UserRepository userRepo = new UserRepository();
            PublicacionRepository pubRepo = new PublicacionRepository();
            OfertaRepository ofertaRepo = new OfertaRepository();
            persistence.ChatRepository chatRepo = new persistence.ChatFileRepository();
            ReporteRepository reporteRepo = new ReporteRepository();

            // 2. Servicios
            UserService userService = new UserService(userRepo);
            PublicacionService pubService = new PublicacionService(pubRepo, userService, ofertaRepo);
            OfertaService ofertaService = new OfertaService(ofertaRepo, userService, pubService);
            AdminService adminService = new AdminService(userRepo, pubRepo, ofertaRepo);
            ReporteService reporteService = new ReporteService(reporteRepo, userRepo);

            // 3. Controladores
            controller.ChatController chatController = new controller.ChatController(chatRepo, ofertaRepo, pubRepo);
            AuthController authController = new AuthController(userService);
            PublicacionController pubController = new PublicacionController(pubService, ofertaService, chatController);
            AdminController adminController = new AdminController(adminService);
            ReporteController reporteController = new ReporteController(reporteService);

            // 4. Vista Principal
            MainWindow main = new MainWindow(authController, pubController, chatController, adminController,
                    reporteController);
            main.setVisible(true);
        });
    }
}
