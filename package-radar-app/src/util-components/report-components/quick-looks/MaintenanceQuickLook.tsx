import { PiWrenchFill } from "react-icons/pi";
import "../quick-looks/QuickLookStyles.css";

interface MaintenanceQuickLookProps{
    data: JSON|undefined;
}
function MaintenanceQuickLook({data}: MaintenanceQuickLookProps){

    // function deriveCommitFreq(){
    //     //const commits_last_30_days: number = data?["commit_info"]["commits_last_30_days"];
    // }
    
    return(
        <div className="quick-look-widget">
            <div className="quick-look-title-container">
                <PiWrenchFill className="quick-look-icon"/>
                <p className="quick-look-title">Maintenance</p>
            </div>
            <div>
                <p>Commit Frequency</p>
                <p></p>

                <p>Last Release</p>
                <p>PLACE HOLDER</p>

                <p>Open Issues</p>
                <p>PLACE HOLDER</p>
            </div>
        </div>
    );
}

export default MaintenanceQuickLook;