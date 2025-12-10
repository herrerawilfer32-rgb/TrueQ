/**
 * Clase: AdminController
 * Controlador encargado de gestionar las acciones administrativas del sistema.Permite a un usuario administrador realizar operaciones como listar, buscar,actualizar y eliminar usuarios, publicaciones y ofertas, tambien cambiar roles y obtener estadísticas generales.
 * @Author Anggel Leal, Wilfer Herrera, David Santos
 * @Version 1
 */

package controller;

import model.User;
import model.Publicacion;
import model.Oferta;
import service.AdminService;
import util.RolUsuario;

import java.util.List;

public class AdminController {

    private final AdminService adminService;
    
   /**
     * Constructor que recibe el servicio encargado de la lógica administrativa.
     */
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
   /**
     * Devuelve una lista con todos los usuarios del sistema.
     * El administrador puede usarla para ver o gestionar cuentas.
     */
    public List<User> listarTodosLosUsuarios(String idAdmin) {
        return adminService.listarTodosLosUsuarios(idAdmin);
    }
 
    public User buscarUsuarioPorId(String idUsuario, String idAdmin) {
        return adminService.buscarUsuarioPorId(idUsuario, idAdmin);
    }
    
 /**
     * Actualiza los datos de un usuario, devuelve true si la operación se hizo bien.
     */
    public boolean actualizarUsuario(User usuario, String idAdmin) {
        try {
            return adminService.actualizarUsuario(usuario, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUsuario(String idUsuario, String idAdmin) {
        try {
            return adminService.eliminarUsuario(idUsuario, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 /**
     * Elimina un usuario del sistema,retorna true si el usuario fue eliminado correctamente.
     */
    public boolean cambiarRolUsuario(String idUsuario, RolUsuario nuevoRol, String idAdmin) {
        try {
            return adminService.cambiarRolUsuario(idUsuario, nuevoRol, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lista todas las publicaciones creadas en el sistema.
     */
    public List<Publicacion> listarTodasLasPublicaciones(String idAdmin) {
        return adminService.listarTodasLasPublicaciones(idAdmin);
    }

    public boolean eliminarPublicacion(String idPublicacion, String idAdmin) {
        try {
            return adminService.eliminarPublicacion(idPublicacion, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public List<Oferta> listarTodasLasOfertas(String idAdmin) {
        return adminService.listarTodasLasOfertas(idAdmin);
    }

    public boolean eliminarOferta(String idOferta, String idAdmin) {
        try {
            return adminService.eliminarOferta(idOferta, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cuenta cuántos usuarios hay registrados
     */
    public int contarUsuarios(String idAdmin) {
        return adminService.contarUsuarios(idAdmin);
    }
     /**
     * Cuenta todas las publicaciones existentes
     */
    public int contarPublicaciones(String idAdmin) {
        return adminService.contarPublicaciones(idAdmin);
    }
     /**
     * Cuenta todas las ofertas.
     */

    public int contarOfertas(String idAdmin) {
        return adminService.contarOfertas(idAdmin);
    }
}
