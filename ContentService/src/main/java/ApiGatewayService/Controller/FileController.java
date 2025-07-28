package ApiGatewayService.Controller;

import ApiGatewayService.Repo.FileRepository;
import ApiGatewayService.Service.FileService;
import ApiGatewayService.model.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService fileService;


    //File upload
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileUpload fileUpload = new FileUpload();

            fileUpload.setFileName(file.getOriginalFilename());
            fileUpload.setFileType(file.getContentType());
            fileUpload.setData(file.getBytes());

            fileRepository.save(fileUpload);

            return ResponseEntity.ok("File successfully completed");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("File upload failed");
        }
    }

    //Specific File Download
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        FileUpload file = fileService.getFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getData());
    }

    @GetMapping("/view/{file_name}")
    public ResponseEntity<byte[]> viewVideo(@PathVariable String file_name) {
        Optional<FileUpload> optionalFile = fileRepository.findByFileName(file_name);

        if (optionalFile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FileUpload file = optionalFile.get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .body(file.getData());
    }
}
