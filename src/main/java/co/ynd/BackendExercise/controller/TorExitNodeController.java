package co.ynd.BackendExercise.controller;

import co.ynd.BackendExercise.config.BaseUrls;
import co.ynd.BackendExercise.model.TorExitNodeResponse;
import co.ynd.BackendExercise.service.TorExitNodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static co.ynd.BackendExercise.config.Messages.*;

@RestController
@RequestMapping(BaseUrls.IP)
public class TorExitNodeController {

    private final TorExitNodeService torExitNodeService;

    public TorExitNodeController(TorExitNodeService torExitNodeService) {
        this.torExitNodeService = torExitNodeService;
    }


    @RequestMapping(value = BaseUrls.IP_PATH, method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkTorExitNode(@PathVariable String ip) {
        if (torExitNodeService.ipAddressIsNotValid(ip)) {
            return ResponseEntity.badRequest().build();
        }
        Set<String> torExitNodes = torExitNodeService.getTorExitNodes();
        if (torExitNodes.contains(ip)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(BaseUrls.IP_PATH)
    public ResponseEntity<TorExitNodeResponse> getTorExitNodeInfo(@PathVariable String ip) {
        if (torExitNodeService.ipAddressIsNotValid(ip)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TorExitNodeResponse(ip, false, IP_ADDRESS_IS_NOT_VALID));
        }
        Set<String> torExitNodes = torExitNodeService.getTorExitNodes();
        if (torExitNodes.contains(ip)) {
            return ResponseEntity.ok(new TorExitNodeResponse(ip, true, IP_ADDRESS_IS_A_TOR_EXIT_NODE));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new TorExitNodeResponse(ip, false, IP_ADDRESS_IS_NOT_A_TOR_EXIT_NODE));
    }
}