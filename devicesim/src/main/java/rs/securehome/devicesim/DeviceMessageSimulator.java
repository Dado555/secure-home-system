package rs.securehome.devicesim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import rs.securehome.common.dto.DeviceMessageDTO;
import rs.securehome.common.enums.MessageType;
import rs.securehome.devicesim.clients.DeviceMessageClient;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Component
public class DeviceMessageSimulator implements ApplicationRunner {

    private final DeviceMessageClient deviceMessageClient;

    static IvParameterSpec iv = new IvParameterSpec(new byte[]{15, 93, 67, 92, 93, 64, 28, 80, 6, 88, 38, 98, 26, 12, 15, 59});

    private SecretKey secretKey;

    @Autowired
    public DeviceMessageSimulator(DeviceMessageClient deviceMessageClient) {
        this.deviceMessageClient = deviceMessageClient;
    }

    private String encodeMessage(String message) throws Exception {
        Cipher aesCipherEnc = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipherEnc.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return Base64Utils.encodeToString(aesCipherEnc.doFinal(message.getBytes()));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode("MO3tr1rvVi3uGOTrcYiGmuzCO1l+okp3N4QVDPdf7Xg=");
        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        Random random = new Random();

        var normalTemperatureMeasurements = new String[]{"21", "22", "23", "24", "25", "26"};
        var extremeTemperatureMeasurements = new String[]{"0", "1", "2", "40", "41", "42"};

        var doorSensorNormalReadings = new String[]{"opened", "closed", "opened", "opened", "closed"};
        var doorSensorAttackReadings = new String[]{"closed", "opened", "opened", "opened", "opened"};

        var thermometerIDs = new int[]{1, 2, 3};
        var doorSensorIDs = new int[]{5};
        var spotlightSensorIDs = new int[]{6};
        var smokeSensorIDs = new int[]{4};

        Runnable thermometerNormalSim = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(thermometerIDs[random.nextInt(thermometerIDs.length)], encodeMessage("The temperature is: " + normalTemperatureMeasurements[random.nextInt(normalTemperatureMeasurements.length - 1)] + "C."), MessageType.INFO, LocalDateTime.now()));
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(1, encodeMessage("The temperature is: 80F."), MessageType.INFO, LocalDateTime.now())); // ovo se salje ali se ne ocitava
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace(); // Ovo ako se baci nesto gadno ne valja
                }
            }
        };

        Runnable doorNormalSim = () -> {
            for (int i = 0; i < 5; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(doorSensorIDs[0], encodeMessage("Door " + doorSensorNormalReadings[i] + "."), MessageType.INFO, LocalDateTime.now()));
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable smokeNormalSim = () -> {
            for (int i = 0; i < 2; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(smokeSensorIDs[0], encodeMessage("No smoke."), MessageType.INFO, LocalDateTime.now()));
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable motionNormalSim = () -> {
            for (int i = 0; i < 5; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(spotlightSensorIDs[0], encodeMessage("Motion detected."), MessageType.INFO, LocalDateTime.now()));
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread threadThermometerNormal = new Thread(thermometerNormalSim);
        Thread threadDoorNormal = new Thread(doorNormalSim);
        Thread threadSmokeNormal = new Thread(smokeNormalSim);
        Thread threadMotionNormal = new Thread(motionNormalSim);
        threadThermometerNormal.start();
        threadDoorNormal.start();
        threadSmokeNormal.start();
        threadMotionNormal.start();

        // Mora sve da se saceka da odradi prije "napada"
        threadThermometerNormal.join();
        threadDoorNormal.join();
        threadSmokeNormal.join();
        threadMotionNormal.join();

        // Шоутајм
        Runnable thermometerExtremeSim = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(thermometerIDs[random.nextInt(thermometerIDs.length - 1)], encodeMessage("The temperature is: " + extremeTemperatureMeasurements[random.nextInt(normalTemperatureMeasurements.length - 1)] + "C."), MessageType.INFO, LocalDateTime.now()));
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //TODO: ako ima vise od 3 openeda u poslednjih x sekundi
        Runnable doorAttackSim = () -> {
            for (int i = 0; i < 5; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(doorSensorIDs[0], encodeMessage("Door " + doorSensorAttackReadings[i] + "."), MessageType.INFO, LocalDateTime.now()));
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //TODO: ovo je samo ako postoji detekcija
        Runnable smokeFireSim = () -> {
            for (int i = 0; i < 2; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(smokeSensorIDs[0], encodeMessage("Smoke detected."), MessageType.WARNING, LocalDateTime.now()));
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //TODO: ovo je samo ako postoji detekcija u nekom vremenskom opsegu
        Runnable motionSuspiciousSim = () -> {
            for (int i = 0; i < 5; i++) {
                try {
                    deviceMessageClient.sendMessage(new DeviceMessageDTO(spotlightSensorIDs[0], encodeMessage("Motion detected."), MessageType.INFO, LocalDateTime.now()));
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread threadThermometerExtreme = new Thread(thermometerExtremeSim);
        Thread threadDoorAttack = new Thread(doorAttackSim);
        Thread threadSmokeFire = new Thread(smokeFireSim);
        Thread threadMotionSuspicious = new Thread(motionSuspiciousSim);
        threadThermometerExtreme.start();
        threadDoorAttack.start();
        threadSmokeFire.start();
        threadMotionSuspicious.start();
    }
}
