import { PiWrenchFill } from "react-icons/pi";
import "../quick-looks/QuickLookStyles.css";
import type { NpmFetchSuccessResponse } from "../../../NPMFetchHook/NpmFetchSuccessResponse";
import formatNumber from "../../NumFormatter";

interface MaintenanceQuickLookProps{
    data: NpmFetchSuccessResponse|undefined;
}
function MaintenanceQuickLook({data}: MaintenanceQuickLookProps){
    const commits_last_90_days: number|undefined = data?.raw_signals?.git_hub_reg?.commit_info?.commits_last_90_days;
    const commits_last_30_days: number|undefined = data?.raw_signals?.git_hub_reg?.commit_info?.commits_last_30_days;
    const historicalRate: number|undefined = typeof commits_last_90_days == 'number' ? (commits_last_90_days/3): undefined;
    const commit_frequency: string = deriveCommitFreq();
    const commit_momentum: string = dervieCommitMomentum();

    function deriveCommitFreq(){
        if(typeof historicalRate == 'undefined'){
            return "Not Available"
        }
        let commit_frequency: string;

        if (historicalRate === 0) {
            commit_frequency = "Inactive";
        } 
        else if (historicalRate < 2) {
            commit_frequency = "Low";
        } 
        else if (historicalRate <= 10) {
            commit_frequency = "Moderate";
        } 
        else {
            commit_frequency = "High";
        }
        return commit_frequency;
    }

    function dervieCommitMomentum(){
        if(typeof historicalRate == 'undefined' || typeof commits_last_30_days == 'undefined'){
            return "Not Available"
        }
        let commit_momentum: string;

        const recencyRatio = commits_last_30_days/historicalRate;

        if (commits_last_30_days === 0 && commits_last_90_days === 0) {
            commit_momentum = "Inactive";
        } 
        else if (historicalRate === 0) {
            commit_momentum = "increasing";
        } 
        else {
            if (recencyRatio >= 1.25) {
                commit_momentum = "Increasing";
            } else if (recencyRatio <= 0.75) {
                commit_momentum = "Declining";
            } else {
                commit_momentum = "Stable";
            }
        }
        return commit_momentum;
    }
    
    return(
        <div className="quick-look-widget">
            <div className="quick-look-title-container">
                <PiWrenchFill className="quick-look-icon"/>
                <p className="quick-look-title">Maintenance</p>
            </div>
            <div>
                <p className="section-title">Commit Frequency</p>
                <p className="section-hero">{commit_frequency}</p>

                <p className="section-title">Commit Momentum</p>
                <p className="section-hero">{commit_momentum}</p>

                <p className="section-title">Open Issues</p>
                <p className="section-hero">{formatNumber(data?.raw_signals?.git_hub_reg?.repo_info?.open_issues_count) ?? "Not Available"}</p>
            </div>
        </div>
    );
}

export default MaintenanceQuickLook;