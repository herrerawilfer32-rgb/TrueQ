package controller;

import model.User;
import model.Publicacion;
import model.Oferta;
import service.AdminService;
import util.RolUsuario;

import java.util.List;

public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public List<User> listarTodosLosUsuarios(String idAdmin) {
        return adminService.listarTodosLosUsuarios(idAdmin);
    }

    public User buscarUsuarioPorId(String idUsuario, String idAdmin) {
        return adminService.buscarUsuarioPorId(idUsuario, idAdmin);
    }

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

    public boolean cambiarRolUsuario(String idUsuario, RolUsuario nuevoRol, String idAdmin) {
        try {
            return adminService.cambiarRolUsuario(idUsuario, nuevoRol, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public int contarUsuarios(String idAdmin) {
        return adminService.contarUsuarios(idAdmin);
    }

    public int contarPublicaciones(String idAdmin) {
        return adminService.contarPublicaciones(idAdmin);
    }

    public int contarOfertas(String idAdmin) {
        return adminService.contarOfertas(idAdmin);
    }
}
