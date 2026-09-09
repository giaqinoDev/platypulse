import { FaLaptopCode } from "react-icons/fa6";
import "../quick-looks/QuickLookStyles.css";
import type { NpmFetchResponse } from "../../../NPMFetchHook/NpmFetchResponse";
import formatNumber from "../../NumFormatter";

interface ContributorsQuickLookProps{
    data: NpmFetchResponse|undefined;
}
function ContributorsQuickLook({data}: ContributorsQuickLookProps){
    const total_commits: number|undefined = data?.raw_signals?.git_hub_reg?.contributors_info?.total_commits;
    const top_commits: number|undefined = data?.raw_signals?.git_hub_reg?.contributors_info?.top_contributor_commits;

    const top_commits_ratio: number|undefined = data?.raw_signals?.git_hub_reg?.contributors_info?.top_commits_ratio;
    
    return(
        <div className="quick-look-widget">
            <div className="quick-look-title-container">
                <FaLaptopCode className="quick-look-icon"/>
                <p className="quick-look-title">Contribution Info</p>
            </div>
            <div>
                <p className="section-title">Repo Wide Commits</p>
                <p className="section-hero">{formatNumber(total_commits) ?? "Not Available"}</p>

                <p className="section-title">Top Contributor Commits</p>
                <p className="section-hero">{formatNumber(top_commits) ?? "Not Available"}</p>

                <p className="section-title">Top Commits Ratio</p>
                <p className="section-hero">{formatNumber(top_commits_ratio) ?? "Not Available"}</p>
            </div>
        </div>
    );
}

export default ContributorsQuickLook;