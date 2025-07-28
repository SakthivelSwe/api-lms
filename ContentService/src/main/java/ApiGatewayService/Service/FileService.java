package ApiGatewayService.Service;

import ApiGatewayService.Repo.FileRepository;
import ApiGatewayService.model.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FileService {


    @Autowired
    private FileRepository fileRepository;


    //File Download
    public FileUpload getFile(String fileName) {
        return fileRepository.findByFileName(fileName)
                .orElseThrow(() -> new RuntimeException("File not found with ID " + fileName));
    }


}
