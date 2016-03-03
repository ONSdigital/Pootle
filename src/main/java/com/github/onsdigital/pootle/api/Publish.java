package com.github.onsdigital.pootle.api;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.helpers.Json;
import com.github.onsdigital.pootle.json.Result;
import com.github.onsdigital.pootle.storage.FtpPublisher;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import java.io.*;
import java.util.zip.ZipInputStream;

@Api
public class Publish {

    private FtpPublisher publisher = new FtpPublisher();

    @POST
    public Result addFile(HttpServletRequest request,
                          HttpServletResponse response) throws IOException, FileUploadException {

        System.out.println("publish >>>>>>>> request");
        String message = null;
        boolean error = false;

        try {
            // Get the file first because request.getParameter will consume the body of the request:
            FileItem data = getFileItem(request);

            if (data == null) {
                response.setStatus(HttpStatus.BAD_REQUEST_400);

                error = true;
                message = "No data found for published file.";
            }

            boolean zipped = BooleanUtils.toBoolean(request.getParameter("zip"));

            if (!error) {

                System.out.println("publish filename: " + data.getName());

                // Publish
                boolean published;
                if (zipped) {
                    System.out.println("Zipped File: Unzipping...");
                    try (ZipInputStream input = new ZipInputStream(new BufferedInputStream(data.getInputStream()))) {
                        //published = Publisher.addFiles(transaction, uri, input);
                        published = true;
                    }
                    System.out.println("Finished Unzipping.");
                } else {
                    System.out.println("publishing file");

                    String path = "target/";
                    publisher.publish(data, path);

                    published = true;
                    System.out.println("publishing file finished: " + data.getName());
                }

                if (published) {
                    message = "published " + data.getName();
                } else {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    error = true;
                    message = "Sadly was not published.";
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            error = true;
            message = ExceptionUtils.getStackTrace(e);
        }

        Result result = Result.builder().message(message).error(error).build();
        System.out.println("publish <<<<<<<< response: " + Json.format(result));
        return result;
    }

    /**
     * Handles reading the uploaded file.
     *
     * @param request The http request.
     * @return A temp file containing the file data.
     * @throws IOException If an error occurs in processing the file.
     */
    FileItem getFileItem(HttpServletRequest request) throws IOException {
        FileItem result = null;

        // Set up the objects that do all the heavy lifting
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            // Read the items - this will save the values to temp files
            for (FileItem item : upload.parseRequest(request)) {
                if (!item.isFormField()) {
                    result = item;
                    break;
                }
            }
        } catch (Exception e) {
            // item.write throws a general Exception, so specialise it by wrapping with IOException
            throw new IOException("Error processing uploaded file", e);
        }

        return result;
    }
}