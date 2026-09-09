import useFetchSnapShots from "../../FetchSnapShotsHook/useFetchSnapShots";
import "./ReportDashboardNav.css"
interface ReportDashboardNavInterface{
    pkg_id : string|null|undefined;
}
function ReportDashboardNav({pkg_id}: ReportDashboardNavInterface){
    const {loading, getSnapShots, fetchError} = useFetchSnapShots();

    async function handleClick(){
        const response = await getSnapShots(pkg_id);
        console.log(response);
    }
    
    return(
        <div id="dashboard-nav-container">
            <button className="dashboard-nav-button">
                Report
            </button>
            <button className="dashboard-nav-button" onClick={handleClick}>
                Snap Shots
            </button>
        </div>
    );
}

export default ReportDashboardNav;