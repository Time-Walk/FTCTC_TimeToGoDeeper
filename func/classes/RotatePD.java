package org.firstinspires.ftc.teamcode.func.classes;

import com.acmerobotics.dashboard.DashboardCore;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.modules.IMU;
import org.firstinspires.ftc.teamcode.func.classes.superclasses.PD;
import org.firstinspires.ftc.teamcode.modules.Wheelbase;
import org.firstinspires.ftc.teamcode.modules.superclasses.RobotConstruct;

@Config
public class RotatePD {
    PD pd;
    public RobotConstruct R;
    Telemetry telemetry;
    public static double kp = -0.0025;
    public static double kd = -0.00025;
    public static double kr = -0.075;
    public static double ErTarget = .1;
    public static double ErSpeedTarget = .5;
    public void init(RobotConstruct R, LinearOpMode L) {
        pd = new PD();
        pd.init(kp, kd);

        this.R = R;
        this.L = L;

        telemetry = FtcDashboard.getInstance().getTelemetry();
    }
    LinearOpMode L;
    public void rotate(double degrees) {
       // degrees = (degrees * (93.5/90));
        R.imuv2.init();
        R.wb.initEncoderAuto();
        while ( (Math.abs(degrees - R.imuv2.getAngle()) > ErTarget || Math.abs((degrees - R.imuv2.getAngle()) - pd.ErLast) > ErSpeedTarget) && L.opModeIsActive() ) {
            double U = pd.tick(degrees - R.imuv2.getAngle());
            double Rele = kr * Math.signum(degrees-R.imuv2.getAngle());
            R.wb.setMtPower(U+Rele, U+Rele, U+Rele, U+Rele);
            telemetry.addData("U", U);
            telemetry.addData("P", pd.P);
            telemetry.addData("D", pd.D);
            telemetry.addData("angle", R.imuv2.getAngle());
            telemetry.addData("Er", degrees-R.imuv2.getAngle());
            telemetry.update();
        }
        telemetry.addData("LB", R.wb.LB.getCurrentPosition());
        telemetry.addData("RB", R.wb.RB.getCurrentPosition());
        telemetry.update();
        R.wb.setMtZero();
        R.wb.delay(1000);
    }
}