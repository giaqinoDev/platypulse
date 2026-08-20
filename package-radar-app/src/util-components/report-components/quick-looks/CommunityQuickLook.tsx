import { HiMiniUserGroup } from "react-icons/hi2";
import "../quick-looks/QuickLookStyles.css";
import type { NpmFetchResponse } from "../../../NPMFetch/NpmFetchResponse";
import type { DownloadDP } from "../../../NPMFetch/DownloadDP";
import formatNumber from "../../NumFormatter";

interface CommunityQuickLookProps{
    data: NpmFetchResponse|undefined;
}
function CommunityQuickLook({data}: CommunityQuickLookProps){
    const github_stars: number|undefined = data?.raw_signals?.git_hub_reg?.repo_info?.stars_count;
    const github_forks: number|undefined = data?.raw_signals?.git_hub_reg?.repo_info?.forks_count;
    const monthly_downloads: number| undefined = dervieMonthlyDownloads();

    function dervieMonthlyDownloads(){
        const downloads_timeline: DownloadDP[]|null|undefined = data?.raw_signals?.downloads_timeline;
        if(typeof downloads_timeline == 'undefined' || downloads_timeline == null){
            return undefined;
        }
        const downloads_timeline_size = downloads_timeline?.length;
        let downloadsSum: number = 0;
        for(let index = downloads_timeline_size - 5; index < downloads_timeline.length - 1; index++){
            downloadsSum += downloads_timeline[index].volume ?? 0;
        }

        return downloadsSum;
    }
    return(
        <div className="quick-look-widget">
            <div className="quick-look-title-container">
                <HiMiniUserGroup className="quick-look-icon"/>
                <p className="quick-look-title">Community</p>
            </div>
            <div>
                <p className="section-title">GitHub Stars</p>
                <p className="section-hero">{formatNumber(github_stars) ?? "Not Available"}</p>

                <p className="section-title">GitHub Forks</p>
                <p className="section-hero">{formatNumber(github_forks) ?? "Not Available"}</p>

                <p className="section-title">Monthly Downloads</p>
                <p className="section-hero">{formatNumber(monthly_downloads) ?? "Not Available"}</p>
            </div>
        </div>
    );
}

export default CommunityQuickLook;