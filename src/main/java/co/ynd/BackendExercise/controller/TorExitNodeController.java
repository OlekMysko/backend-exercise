package co.ynd.BackendExercise.controller;

import co.ynd.BackendExercise.model.TorExitNodeResponse;
import co.ynd.BackendExercise.service.TorExitNodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/ip")
public class TorExitNodeController {

    private final TorExitNodeService torExitNodeService;
    private static final String IP_PATH = "/{ip}";

    public TorExitNodeController(TorExitNodeService torExitNodeService) {
        this.torExitNodeService = torExitNodeService;
    }


    @RequestMapping(value = IP_PATH, method = RequestMethod.HEAD)
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

    @GetMapping(IP_PATH)
    public ResponseEntity<TorExitNodeResponse> getTorExitNodeInfo(@PathVariable String ip) {
        if (torExitNodeService.ipAddressIsNotValid(ip)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TorExitNodeResponse(ip, false, "The IP address is not valid."));
        }
        Set<String> torExitNodes = torExitNodeService.getTorExitNodes();
        if (torExitNodes.contains(ip)) {
            return ResponseEntity.ok(new TorExitNodeResponse(ip, true, "The IP address is a Tor exit node."));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new TorExitNodeResponse(ip, false, "The IP address is not a Tor exit node."));
    }
}