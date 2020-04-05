package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DbConnect {
    private Connection c;
    private Statement st;
    private PreparedStatement getPassword,getFile,getMessages,checkLogin,insertUser,getPeopleByEmail,getPhoto,searchPeople,sendMessage;
    public DbConnect() throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        c=DriverManager.getConnection(
   "jdbc:mysql://localhost:3306/ptalk","root","incapp");
        st=c.createStatement();
        checkLogin=c.prepareStatement(
    "select * from userinfo where email=? and pass=?");
        insertUser=c.prepareStatement(
    "insert into userinfo values(?,?,?,?,?,?,?,?,?,?)");
        getPassword=c.prepareStatement(
    "select pass from userinfo where email=? ");
        getPhoto=c.prepareStatement(
    "select photo from userinfo where email=? ");
        getPeopleByEmail=c.prepareStatement(
    "select * from userinfo where email=? ");
        searchPeople=c.prepareStatement(
    "select name,email,phone,dob,gender from userinfo where  state=? and city=? and email!=? and area like ? ");
    sendMessage=c.prepareStatement(
    "insert into peoplemsg  (sid,rid,msg,filename,ufile,udate) values (?,?,?,?,?,now())");
    getMessages=c.prepareStatement(
    "select * from peoplemsg where sid=? and rid=? ");
    getFile=c.prepareStatement(
    "select ufile from peoplemsg where pid=? ");
    }
    public String sendMessage(String s,String r,String m,String f,java.io.InputStream in) throws SQLException {        
        sendMessage.setString(1, s);
        sendMessage.setString(2, r);
        sendMessage.setString(3, m);
        sendMessage.setString(4, f);
        sendMessage.setBinaryStream(5, in);
        int x=sendMessage.executeUpdate();
        if(x==1)
         return "Done";
        else 
         return "Error";
    }
    public HashMap checkLogin(String e,String p) throws SQLException{
        checkLogin.setString(1, e);
        checkLogin.setString(2, p);
        ResultSet rs=checkLogin.executeQuery();
        if(rs.next()){
            HashMap userDetails=new HashMap();
            userDetails.put("email", rs.getString("email"));
            userDetails.put("name", rs.getString("name"));
            userDetails.put("phone", rs.getString("phone"));
            userDetails.put("gender", rs.getString("gender"));
            userDetails.put("dob", rs.getDate("dob"));
            userDetails.put("state", rs.getString("state"));
            userDetails.put("city", rs.getString("city"));
            userDetails.put("area", rs.getString("area"));
            return userDetails;
        }else{
            return null;
        }
    }
    public String insertUser(HashMap userDetails)throws SQLException{
        try{
        insertUser.setString(1, (String)userDetails.get("email"));
        insertUser.setString(2, (String)userDetails.get("pass"));
        insertUser.setString(3, (String)userDetails.get("name"));
        insertUser.setString(4, (String)userDetails.get("phone"));
        insertUser.setString(5, (String)userDetails.get("gender"));
        insertUser.setDate(6, (java.sql.Date)userDetails.get("dob"));
        insertUser.setString(7, (String)userDetails.get("state"));
        insertUser.setString(8, (String)userDetails.get("city"));
        insertUser.setString(9, (String)userDetails.get("area"));
        insertUser.setBinaryStream(10, (InputStream)userDetails.get("photo"));
        int x=insertUser.executeUpdate();
        if(x!=0)
           return "Success";
        else
            return "Failed";
        }catch(java.sql.SQLIntegrityConstraintViolationException ex){
            return "Already";
        }
    }
    public byte[] getPhoto(String e){
        try{
            getPhoto.setString(1, e);
            ResultSet rs=getPhoto.executeQuery();
            if(rs.next()){
                byte[] b=rs.getBytes("photo");
                if(b.length!=0)
                    return b;
                else
                    return null;
            }else{
                return null;
            }
        }catch(Exception ex){
            return null;
        }
    }
    public String getPassword(String e){
        try{
            getPassword.setString(1, e);
            ResultSet rs=getPassword.executeQuery();
            if(rs.next()){
                    return rs.getString("pass");
            }else{
                return null;
            }
        }catch(Exception ex){
            return null;
        }
    }
    public HashMap getPeopleByEmail(String e){
        try{
            getPeopleByEmail.setString(1, e);
            ResultSet rs=getPeopleByEmail.executeQuery();
            if(rs.next()){
            HashMap userDetails=new HashMap();
            userDetails.put("email", rs.getString("email"));
            userDetails.put("name", rs.getString("name"));
            userDetails.put("phone", rs.getString("phone"));
            userDetails.put("gender", rs.getString("gender"));
            userDetails.put("dob", rs.getDate("dob"));
            userDetails.put("state", rs.getString("state"));
            userDetails.put("city", rs.getString("city"));
            userDetails.put("area", rs.getString("area"));
            return userDetails;
        }else{
            return null;
        }
        }catch(Exception ex){
            return null;
        }
    }
    public java.util.ArrayList<java.util.HashMap> searchPeople(String s,String c,String e,String a) throws SQLException{     
searchPeople.setString(1, s);
searchPeople.setString(2, c);
searchPeople.setString(3, e);
searchPeople.setString(4, "%"+a+"%");
ResultSet r=searchPeople.executeQuery();
java.util.ArrayList<java.util.HashMap> allUserDetails=
        new java.util.ArrayList();
while(r.next()){
    java.util.HashMap UserDetails=new java.util.HashMap();
    UserDetails.put("email",r.getString("email"));
    UserDetails.put("name",r.getString("name"));
    UserDetails.put("phone",r.getString("phone"));
    UserDetails.put("gender",r.getString("gender"));
    UserDetails.put("dob",r.getDate("dob"));
    allUserDetails.add(UserDetails);
    }
return allUserDetails;
    }
    public java.util.ArrayList<java.util.HashMap> getMessages(String s,String r) throws SQLException{     
getMessages.setString(1, s);
getMessages.setString(2, r);
ResultSet rs=getMessages.executeQuery();
java.util.ArrayList<java.util.HashMap> allMessage=
        new java.util.ArrayList();
while(rs.next()){
    java.util.HashMap message=new java.util.HashMap();
    message.put("message",rs.getString("msg"));
    message.put("datetime",rs.getString("udate"));
    message.put("filename",rs.getString("filename"));
    message.put("file",rs.getBytes("ufile"));
    message.put("pid",rs.getString("pid"));
    allMessage.add(message);
    }
return allMessage;
    }
    public byte[] getFile(int e){
        try{
            getFile.setInt(1, e);
            ResultSet rs=getFile.executeQuery();
            if(rs.next()){
                byte[] b=rs.getBytes("ufile");
                if(b.length!=0)
                    return b;
                else
                    return null;
            }else{
                return null;
            }
        }catch(Exception ex){
            return null;
        }
    }
}
