/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import DAO.UsuarioDAO;
import Model.Usuario;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author ice
 */
public class s1 extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, FileUploadException, Exception {

        String origem = request.getParameter("origem");
        HttpSession session = request.getSession(true);

        if (session.getAttribute("ativo") == null && !origem.equals("login")) {
            request.getSession().setAttribute("erro", "Sua sessão deve ter expirado");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        //A requisição veio da tela de login
        if ("login".equals(origem)) {
            Usuario u = null;
            String matriculaFormulario = request.getParameter("login");
            String senhaFormulario = request.getParameter("senha");
            if (isInteger(matriculaFormulario)) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                u = usuarioDAO.getUsuario(Integer.parseInt(matriculaFormulario));
                if (u != null) {          // Se o usuário existir
                    String senhaUsuario = u.getSenha();
                    if (senhaUsuario.equals(senhaFormulario)) {   // Verifica senha
                        request.getSession().setAttribute("ativo", "esta_ativo");
                        request.getRequestDispatcher("menu.jsp").forward(request, response); //mudar de página
                    } else {   // Senha errada, incluir o atributo erro no objeto session
                        request.getSession().setAttribute("erro", "senha errada");
                        request.getRequestDispatcher("login.jsp").forward(request, response); //mudar de página
                    }
                } else {   // Usuario nõ existe no banco
                    request.getSession().setAttribute("erro", "usuario nao existe");
                    request.getRequestDispatcher("login.jsp").forward(request, response); //mudar de página
                }
            } else {       // Matricula fornecida não é um inteiro
                request.getSession().setAttribute("erro", "usuario nao existe");
                request.getRequestDispatcher("login.jsp").forward(request, response); //mudar de página
            }
        } else if (origem.equals("menu")) {     // A requisição veio do menu
            session.removeAttribute("ativo");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else if (origem.equals("lista") || origem.equals("rotator") || origem.equals("upload")) {
            request.getRequestDispatcher("menu.jsp").forward(request, response);
        } else if (origem.equals("upload_arquivo")) {
            
            String filePath = "/ice/NetBeansProjects/DCC-Lab-Web-192/arquivo-atual/web/docs";
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                                            new DiskFileItemFactory())
                                            .parseRequest((RequestContext) request);

                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        String name = "Teste.pdf";
                        item.write( new File(filePath + File.separator + name));
                    }
                }
               //File uploaded successfully
               request.setAttribute("message", "File Uploaded Successfully");
            } catch (Exception ex) {
               request.setAttribute("message", "File Upload Failed due to " + ex);
            }
     

            
            /*
            String file_name = "local.pdf";
            File file = null;
            int maxxFileSize = 10000 * 1024;
            int maxMemSize = 1000 * 1024;
            ServletContext servletContext = getServletContext();
            String filePath = "/ice/NetBeansProjects/DCC-Lab-Web-192/arquivo-atual/web/docs";

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // Configure a repository (to ensure a secure temp location is used)
            factory.setSizeThreshold(maxMemSize);
            factory.setRepository(new File(filePath));
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(maxxFileSize);
            // Parse the request
            List<FileItem> fileItens = null;
            try {
                fileItens = upload.parseRequest((RequestContext) request);
                for (FileItem item : fileItens) {
                    if (!item.isFormField()) {
                        String name = "Teste.pdf";
                        item.write(new File(filePath + File.separator + name));
                        
                        String fileName = item.getFieldName();
                        if (fileName.lastIndexOf("\\") >= 0) {
                            file = new File(filePath + fileName);
                        } else {
                            file = new File(filePath + fileName);
                        }
                        try {
                            item.write(file);
                        } catch (Exception ex) {
                            Logger.getLogger(s1.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (FileUploadException e) {

            }
            */
            
            request.getRequestDispatcher("upload_de_arquivo.jsp").forward(request, response);
        }

        response.setContentType("text/html;charset=UTF-8");
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FileUploadException ex) {
            Logger.getLogger(s1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(s1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (FileUploadException ex) {
            Logger.getLogger(s1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(s1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
