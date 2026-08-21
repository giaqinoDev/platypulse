import { FaGitAlt } from "react-icons/fa";
import "../quick-looks/QuickLookStyles.css";
import type { NpmFetchResponse } from "../../../NPMFetch/NpmFetchResponse";
import formatNumber from "../../NumFormatter";

interface RepositoryQuickLookProps{
    data: NpmFetchResponse|undefined;
}
function RepositoryQuickLook({data}: RepositoryQuickLookProps){
    const open_issues: number|undefined = data?.raw_signals?.git_hub_reg?.repo_info?.open_issues_count;
    const closed_issues: number|undefined = data?.raw_signals?.git_hub_reg?.repo_info?.closed_issues_count;

    const days_since_last_push: number|undefined = data?.raw_signals?.git_hub_reg?.repo_info?.days_since_last_push;
    
    return(
        <div className="quick-look-widget">
            <div className="quick-look-title-container">
                <FaGitAlt className="quick-look-icon"/>
                <p className="quick-look-title">Repository Info</p>
            </div>
            <div>
                <p className="section-title">Open Issues</p>
                <p className="section-hero">{formatNumber(open_issues) ?? "Not Available"}</p>

                <p className="section-title">Closed Issues</p>
                <p className="section-hero">{formatNumber(closed_issues) ?? "Not Available"}</p>

                <p className="section-title">Days Since Last Push</p>
                <p className="section-hero">{formatNumber(days_since_last_push) ?? "Not Available"}</p>
            </div>
        </div>
    );
}

export default RepositoryQuickLook;