package servlets;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
@MultipartConfig
public class SendMessage extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
            HttpSession session=request.getSession();
            HashMap userDetails=(HashMap)session.getAttribute("userDetails");
            if(userDetails!=null){
                String temail=request.getParameter("temail");
                String semail=(String)userDetails.get("email");
                String message=request.getParameter("message");
                Part p=request.getPart("ufile");
                java.io.InputStream in=null;
                String fname="";
                if(p!=null){
                    fname=p.getSubmittedFileName();
                    in = p.getInputStream();
                }
                db.DbConnect db=new db.DbConnect();
                String r=db.sendMessage(semail, temail, message, fname, in);
                if(r.equalsIgnoreCase("Done")){
                    session.setAttribute("msg", "Message Sent Successfully!");
                }else{
                    session.setAttribute("msg", "Message Sending Failed!");
                }
                response.sendRedirect("talk.jsp?temail="+temail);
            }else{
                session.setAttribute("msg", "Plz login First!");
                response.sendRedirect("home.jsp");
            }
        }catch(Exception ex){
            
        }
    }
}
