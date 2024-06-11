package android;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;

@MultipartConfig(maxFileSize = 1024 * 1024 * 10, location = "c:/upload/") //업로드 설정 (c:/파일저장경로)
public class UploadController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = "-";
		int filesize = 0;
		try {
			for (Part part : request.getParts()) { //multipart/form-data
				filename = part.getSubmittedFileName(); //첨부파일 이름
				if (filename != null && !filename.equals("null") && !filename.equals("")) {
					filesize = (int) part.getSize(); //파일사이즈
					part.write(filename); //파일 저장(지정한 저장경로에)
					response.getWriter().println("success! fileName:" + filename); //브라우저에 출력
					break;
				}
			}
		} catch (Exception e) {
		response.getWriter().println("error:" + e.toString()); 
		e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
