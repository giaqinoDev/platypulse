import { type Dispatch, type SetStateAction } from "react";
import "./ReportDashboardNav.css"
interface ReportDashboardNavInterface{
    // pkg_id : string|null|undefined;
    handleSnapShotRetreival: () => Promise<void>;
    setReportActiveNav: Dispatch<SetStateAction<string>>;
}
function ReportDashboardNav({handleSnapShotRetreival, setReportActiveNav}: ReportDashboardNavInterface){
    // const [snapshotsRetrieved, setSnapshotsRetrieved] = useState<boolean>(false);
    // const {loading, getSnapShots, fetchError} = useFetchSnapShots();

    // async function handleClick(){
    //     if(!snapshotsRetrieved){
    //         const response = await getSnapShots(pkg_id);
    //         setSnapshotsRetrieved(true);
    //     }
    // }
    
    return(
        <div id="dashboard-nav-container">
            <button className="dashboard-nav-button" onClick={() => setReportActiveNav("report")}>
                Report
            </button>
            <button className="dashboard-nav-button" onClick={() => {handleSnapShotRetreival(); setReportActiveNav("snapshots");}}>
                Snap Shots
            </button>
        </div>
    );
}

export default ReportDashboardNav;